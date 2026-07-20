package com.divineworkshop.rare;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.UUID;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = "divineworkshop")
public class ModRareEventHandler {

    private static final UUID ATTACK_SPEED_MODIFIER_UUID = UUID.fromString("da5ef216-209d-472e-8356-8a032d1645a2");
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("219bf629-9ba4-41d3-a09c-36ba951f21bc");
    private static final UUID TOUGHNESS_MODIFIER_UUID = UUID.fromString("fb76189b-449e-4c74-904d-e9c5225fa5bd");
    private static final UUID TRACING_WIND_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel SUSTENANCE_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("com.divineworkshop:sustenance_network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerSustenancePackets() {
        int id = 0;
        SUSTENANCE_CHANNEL.registerMessage(id++, SyncSustenanceTimerPacket.class,
                SyncSustenanceTimerPacket::encode,
                SyncSustenanceTimerPacket::decode,
                SyncSustenanceTimerPacket::handle);
    }

    public static class SyncSustenanceTimerPacket {
        private final int entityId;
        private final int remainingTicks;
        public SyncSustenanceTimerPacket(int entityId, int remainingTicks) {
            this.entityId = entityId;
            this.remainingTicks = remainingTicks;
        }
        public static void encode(SyncSustenanceTimerPacket msg, FriendlyByteBuf buf) {
            buf.writeInt(msg.entityId);
            buf.writeInt(msg.remainingTicks);
        }
        public static SyncSustenanceTimerPacket decode(FriendlyByteBuf buf) {
            return new SyncSustenanceTimerPacket(buf.readInt(), buf.readInt());
        }
        public static void handle(SyncSustenanceTimerPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.level != null) {
                    net.minecraft.world.entity.Entity entity = mc.level.getEntity(msg.entityId);
                    if (entity instanceof LivingEntity living) {
                        living.getPersistentData().putInt("SustenanceTimer", msg.remainingTicks);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    private void sendSustenanceSync(LivingEntity entity, int remainingTicks) {
        if (entity == null || entity.level().isClientSide) return;
        SUSTENANCE_CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),
                new SyncSustenanceTimerPacket(entity.getId(), remainingTicks));
        if (entity instanceof ServerPlayer player) {
            SUSTENANCE_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                    new SyncSustenanceTimerPacket(entity.getId(), remainingTicks));
        }
    }

    @SubscribeEvent
    public static void onLivingTickSustenance(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide) return;
        if (!ModRareConfig.ENABLE_SUSTENANCE.get()) return;

        ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
        int level = EnchantmentHelper.getItemEnchantmentLevel(ModRareEnchantments.SUSTENANCE.get(), helmet);
        if (level <= 0) return;

        CompoundTag data = entity.getPersistentData();
        int interval, duration;
        if (level == 1) {
            interval = 3600;
            duration = 200;
        } else if (level == 2) {
            interval = 1200;
            duration = 300;
        } else if (level == 3) {
            interval = 600;
            duration = 400;
        } else {
            interval = 400;
            duration = 600;
        }

        int timer = data.getInt("SustenanceTimer");
        if (timer <= 0) {
            timer = interval;
            data.putInt("SustenanceTimer", timer);
        }
        if (timer <= 0) {
            entity.addEffect(new MobEffectInstance(MobEffects.SATURATION, duration, 0, false, false, true));
            timer = interval;
        }
        timer--;
        data.putInt("SustenanceTimer", timer);

        if (entity.tickCount % 20 == 0 && entity instanceof Player) {
            new ModRareEventHandler().sendSustenanceSync(entity, timer);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onItemDestroy(PlayerDestroyItemEvent event) {
        Player player = event.getEntity();
        if (player == null || player.level().isClientSide) return;
        ItemStack stack = event.getOriginal();
        if (!ModRareConfig.ENABLE_PRACTICE_MAKES_PERFECT.get()) return;
        int unbreakingLvl = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
        if (unbreakingLvl >= 3) {
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantedBookItem.addEnchantment(book, new EnchantmentInstance(ModRareEnchantments.PRACTICE_MAKES_PERFECT.get(), 1));
            player.level().addFreshEntity(new ItemEntity(player.level(), player.getX(), player.getY() + 0.5, player.getZ(), book));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null || player.level().isClientSide) return;
        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof DiggerItem)) return;
        int level = EnchantmentHelper.getItemEnchantmentLevel(ModRareEnchantments.PRACTICE_MAKES_PERFECT.get(), mainHand);
        if (level <= 0) return;
        CompoundTag tag = mainHand.getOrCreateTag();
        int current = tag.getInt("PMP_Digs");
        tag.putInt("PMP_Digs", current + 1);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (player == null) return;
        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof DiggerItem)) return;
        int level = EnchantmentHelper.getItemEnchantmentLevel(ModRareEnchantments.PRACTICE_MAKES_PERFECT.get(), mainHand);
        if (level <= 0) return;
        CompoundTag tag = mainHand.getTag();
        if (tag == null) return;
        int digs = tag.getInt("PMP_Digs");
        int threshold = (int) (ModRareConfig.DIG_THRESHOLD_BASE.get() * Math.pow(ModRareConfig.THRESHOLD_BASE_B.get(), level - 1));
        if (digs >= threshold) {
            double boost = 0.30 * Math.pow(ModRareConfig.BOOST_BASE_C.get(), level - 1);
            event.setNewSpeed(event.getOriginalSpeed() * (float)(1.0 + boost));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof Player player && !player.level().isClientSide) {
            ItemStack mainHand = player.getMainHandItem();
            if (mainHand.getItem() instanceof SwordItem || mainHand.getItem() instanceof BowItem ||
                mainHand.getItem() instanceof TridentItem || mainHand.getItem() instanceof CrossbowItem) {
                int level = EnchantmentHelper.getItemEnchantmentLevel(ModRareEnchantments.PRACTICE_MAKES_PERFECT.get(), mainHand);
                if (level <= 0) return;
                CompoundTag tag = mainHand.getOrCreateTag();
                int current = tag.getInt("PMP_Attacks");
                tag.putInt("PMP_Attacks", current + 1);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            ItemStack mainHand = player.getMainHandItem();
            if (mainHand.getItem() instanceof SwordItem || mainHand.getItem() instanceof BowItem ||
                mainHand.getItem() instanceof TridentItem || mainHand.getItem() instanceof CrossbowItem) {
                int level = EnchantmentHelper.getItemEnchantmentLevel(ModRareEnchantments.PRACTICE_MAKES_PERFECT.get(), mainHand);
                if (level <= 0) return;
                CompoundTag tag = mainHand.getTag();
                if (tag == null) return;
                int attacks = tag.getInt("PMP_Attacks");
                int threshold = (int) (ModRareConfig.ATTACK_THRESHOLD_BASE.get() * Math.pow(ModRareConfig.THRESHOLD_BASE_B.get(), level - 1));
                if (attacks >= threshold) {
                    double boost = 0.10 * Math.pow(ModRareConfig.BOOST_BASE_C.get(), level - 1);
                    event.setAmount(event.getAmount() * (float)(1.0 + boost));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onArmorHurt(LivingDamageEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                    ItemStack armor = player.getItemBySlot(slot);
                    if (armor.getItem() instanceof ArmorItem) {
                        int level = EnchantmentHelper.getItemEnchantmentLevel(ModRareEnchantments.PRACTICE_MAKES_PERFECT.get(), armor);
                        if (level > 0) {
                            CompoundTag tag = armor.getOrCreateTag();
                            int current = tag.getInt("PMP_Hurts");
                            tag.putInt("PMP_Hurts", current + 1);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTickAttributes(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) return;

        AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeed != null) {
            attackSpeed.removeModifier(ATTACK_SPEED_MODIFIER_UUID);
            ItemStack mainHand = player.getMainHandItem();
            int level = EnchantmentHelper.getItemEnchantmentLevel(ModRareEnchantments.PRACTICE_MAKES_PERFECT.get(), mainHand);
            if (level > 0 && (mainHand.getItem() instanceof SwordItem || mainHand.getItem() instanceof BowItem ||
                    mainHand.getItem() instanceof TridentItem || mainHand.getItem() instanceof CrossbowItem)) {
                CompoundTag tag = mainHand.getTag();
                if (tag != null) {
                    int attacks = tag.getInt("PMP_Attacks");
                    int threshold = (int) (ModRareConfig.ATTACK_THRESHOLD_BASE.get() * Math.pow(ModRareConfig.THRESHOLD_BASE_B.get(), level - 1));
                    if (attacks >= threshold) {
                        double boost = 0.10 * Math.pow(ModRareConfig.BOOST_BASE_C.get(), level - 1);
                        attackSpeed.addTransientModifier(new AttributeModifier(ATTACK_SPEED_MODIFIER_UUID,
                                "PMP Attack Speed", boost, AttributeModifier.Operation.MULTIPLY_BASE));
                    }
                }
            }
        }

        AttributeInstance armorAttr = player.getAttribute(Attributes.ARMOR);
        AttributeInstance toughnessAttr = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (armorAttr != null && toughnessAttr != null) {
            armorAttr.removeModifier(ARMOR_MODIFIER_UUID);
            toughnessAttr.removeModifier(TOUGHNESS_MODIFIER_UUID);
            double totalArmorBoost = 0.0;
            double totalToughnessBoost = 0.0;
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                    ItemStack armor = player.getItemBySlot(slot);
                    int level = EnchantmentHelper.getItemEnchantmentLevel(ModRareEnchantments.PRACTICE_MAKES_PERFECT.get(), armor);
                    if (level > 0 && armor.getItem() instanceof ArmorItem armorItem) {
                        CompoundTag tag = armor.getTag();
                        if (tag != null) {
                            int hurts = tag.getInt("PMP_Hurts");
                            int threshold = (int) (ModRareConfig.HURT_THRESHOLD_BASE.get() * Math.pow(ModRareConfig.THRESHOLD_BASE_B.get(), level - 1));
                            if (hurts >= threshold) {
                                double boost = 0.10 * Math.pow(ModRareConfig.BOOST_BASE_C.get(), level - 1);
                                totalArmorBoost += armorItem.getDefense() * boost;
                                totalToughnessBoost += armorItem.getToughness() * boost;
                            }
                        }
                    }
                }
            }
            if (totalArmorBoost > 0) {
                armorAttr.addTransientModifier(new AttributeModifier(ARMOR_MODIFIER_UUID,
                        "PMP Armor Boost", totalArmorBoost, AttributeModifier.Operation.ADDITION));
            }
            if (totalToughnessBoost > 0) {
                toughnessAttr.addTransientModifier(new AttributeModifier(TOUGHNESS_MODIFIER_UUID,
                        "PMP Toughness Boost", totalToughnessBoost, AttributeModifier.Operation.ADDITION));
            }
        }

        AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (moveSpeed != null) {
            moveSpeed.removeModifier(TRACING_WIND_UUID);
            ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
            int level = EnchantmentHelper.getItemEnchantmentLevel(ModRareEnchantments.TRACING_WIND.get(), feet);
            if (level > 0 && ModRareConfig.ENABLE_TRACING_WIND.get()) {
                double boost = ModRareConfig.TRACING_WIND_SPEED_BASE.get() * level;
                if (boost > 0) {
                    moveSpeed.addTransientModifier(new AttributeModifier(TRACING_WIND_UUID,
                            "Tracing Wind Speed", boost, AttributeModifier.Operation.MULTIPLY_BASE));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTickRepair(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide) return;
        if (!ModRareConfig.ENABLE_SOOTHING_REPAIR.get()) return;

        int baseInterval = ModRareConfig.SOOTHING_REPAIR_INTERVAL_BASE.get();
        int tick = entity.tickCount;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.isEmpty()) continue;
            if (!stack.isDamageableItem()) continue;
            int level = EnchantmentHelper.getItemEnchantmentLevel(ModRareEnchantments.SOOTHING_REPAIR.get(), stack);
            if (level <= 0) continue;
            int interval = Math.max(1, baseInterval / level);
            if (tick % interval == 0 && stack.getDamageValue() > 0) {
                stack.setDamageValue(stack.getDamageValue() - 1);
            }
        }

        if (entity instanceof Player player) {
            for (int i = 0; i < 36; i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.isEmpty()) continue;
                if (!stack.isDamageableItem()) continue;
                int level = EnchantmentHelper.getItemEnchantmentLevel(ModRareEnchantments.SOOTHING_REPAIR.get(), stack);
                if (level <= 0) continue;
                int interval = Math.max(1, baseInterval / level);
                if (tick % interval == 0 && stack.getDamageValue() > 0) {
                    stack.setDamageValue(stack.getDamageValue() - 1);
                }
            }
        }
    }
}