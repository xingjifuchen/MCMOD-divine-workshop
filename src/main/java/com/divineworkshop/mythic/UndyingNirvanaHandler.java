package com.divineworkshop.mythic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.UUID;
import java.util.function.Supplier;

public class UndyingNirvanaHandler {
    private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    
    public static final UUID SOUL_REAVER_HEALTH_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    public static final UUID SOUL_REAVER_ATTACK_UUID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f23456789012");

    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("com.divineworkshop:nirvana_network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerPackets() {
        int id = 0;
        INSTANCE.registerMessage(id++, SyncNirvanaPacket.class,
                SyncNirvanaPacket::encode,
                SyncNirvanaPacket::decode,
                SyncNirvanaPacket::handle);
    }

    public static class SyncNirvanaPacket {
        private final int entityId;
        private final int layers;
        private final int maxLayers;
        private final int timer;

        public SyncNirvanaPacket(int entityId, int layers, int maxLayers, int timer) {
            this.entityId = entityId;
            this.layers = layers;
            this.maxLayers = maxLayers;
            this.timer = timer;
        }

        public static void encode(SyncNirvanaPacket msg, FriendlyByteBuf buf) {
            buf.writeInt(msg.entityId);
            buf.writeInt(msg.layers);
            buf.writeInt(msg.maxLayers);
            buf.writeInt(msg.timer);
        }

        public static SyncNirvanaPacket decode(FriendlyByteBuf buf) {
            return new SyncNirvanaPacket(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
        }

        public static void handle(SyncNirvanaPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.level != null) {
                    net.minecraft.world.entity.Entity entity = mc.level.getEntity(msg.entityId);
                    if (entity instanceof LivingEntity living) {
                        CompoundTag nbt = living.getPersistentData();
                        nbt.putInt("NirvanaLayers", msg.layers);
                        nbt.putInt("NirvanaMaxLayers", msg.maxLayers);
                        nbt.putInt("NirvanaRestorationTimer", msg.timer);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    private void sendSyncPacket(LivingEntity entity, int layers, int maxLayers, int timer) {
        if (entity == null || entity.level().isClientSide) return;
        if (maxLayers <= 0) return;
        if (layers < 0) layers = 0;
        if (layers > maxLayers) layers = maxLayers;
        if (timer < 0) timer = 0;

        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),
                new SyncNirvanaPacket(entity.getId(), layers, maxLayers, timer));
        if (entity instanceof ServerPlayer player) {
            INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                    new SyncNirvanaPacket(entity.getId(), layers, maxLayers, timer));
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) return;
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (!isImportantEntity(entity)) return;

        int totalNirvanaLevel = getArmorNirvanaLevel(entity);
        CompoundTag persistentData = entity.getPersistentData();

        if (totalNirvanaLevel == 0) {
            persistentData.putInt("NirvanaLayers", 0);
            persistentData.putInt("NirvanaMaxLayers", 0);
            persistentData.putInt("NirvanaRestorationTimer", 0);
            sendSyncPacket(entity, 0, 0, 0);
            return;
        }

        if (!persistentData.contains("NirvanaLayers")) {
            persistentData.putInt("NirvanaLayers", totalNirvanaLevel);
        }
        int currentLayers = persistentData.getInt("NirvanaLayers");
        if (currentLayers < 0 || currentLayers > totalNirvanaLevel) {
            currentLayers = totalNirvanaLevel;
        }
        persistentData.putInt("NirvanaLayers", currentLayers);
        persistentData.putInt("NirvanaMaxLayers", totalNirvanaLevel);
        if (!persistentData.contains("NirvanaRestorationTimer")) {
            persistentData.putInt("NirvanaRestorationTimer", 0);
        }
        int timer = persistentData.getInt("NirvanaRestorationTimer");
        if (timer < 0) timer = 0;
        persistentData.putInt("NirvanaRestorationTimer", timer);

        sendSyncPacket(entity, currentLayers, totalNirvanaLevel, timer);
    }

    @SubscribeEvent
    public void onEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide) return;
        if (!isImportantEntity(entity)) return;
        if (!ModMythicConfig.ENABLE_UNDYING_NIRVANA.get()) return;

        EquipmentSlot slot = event.getSlot();
        boolean isArmor = slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST ||
                          slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
        if (!isArmor) return;

        int newTotal = getArmorNirvanaLevel(entity);
        CompoundTag persistentData = entity.getPersistentData();
        if (!persistentData.contains("NirvanaLayers")) {
            persistentData.putInt("NirvanaLayers", newTotal);
        } else {
            int current = persistentData.getInt("NirvanaLayers");
            if (current > newTotal) {
                persistentData.putInt("NirvanaLayers", newTotal);
                persistentData.putInt("NirvanaRestorationTimer", 0);
                sendSyncPacket(entity, newTotal, newTotal, 0);
            }
        }
        persistentData.putInt("NirvanaMaxLayers", newTotal);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!ModMythicConfig.ENABLE_UNDYING_NIRVANA.get()) return;
        LivingEntity target = event.getEntity();
        if (target == null || target.level().isClientSide) return;

        if (event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD)) return;

        int totalNirvanaLevel = getArmorNirvanaLevel(target);
        if (totalNirvanaLevel <= 0) return;
        
        CompoundTag persistentData = target.getPersistentData();
        
        if (!persistentData.contains("NirvanaLayers")) {
            persistentData.putInt("NirvanaLayers", totalNirvanaLevel);
        }
        int currentLayers = persistentData.getInt("NirvanaLayers");

        if (currentLayers <= 0) {
            persistentData.putInt("NirvanaLayers", totalNirvanaLevel);
            persistentData.putInt("NirvanaRestorationTimer", 0);
            return;
        }

        event.setCanceled(true);
        int newLayers = currentLayers - 1;
        persistentData.putInt("NirvanaLayers", newLayers);
        persistentData.putInt("NirvanaMaxLayers", totalNirvanaLevel);

        target.setHealth(target.getMaxHealth());

        float factorA = ModMythicConfig.UNDYING_NIRVANA_FACTOR_A.get().floatValue(); 
        int reviveCount = Math.max(1, totalNirvanaLevel - newLayers); 
        
        float absorbAmount = target.getMaxHealth() * factorA * (float) totalNirvanaLevel * (float) reviveCount;
        target.setAbsorptionAmount(target.getAbsorptionAmount() + absorbAmount);

        int buffDurationTicks = (int) (ModMythicConfig.UNDYING_NIRVANA_FACTOR_C.get().doubleValue() * 20);
        int potionAmp = Math.min(4, totalNirvanaLevel) - 1;
        if (potionAmp >= 0) {
            target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, buffDurationTicks, potionAmp));
            target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, buffDurationTicks, potionAmp));
        }
        target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, buffDurationTicks, Math.max(0, totalNirvanaLevel - 1)));

        target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        target.level().broadcastEntityEvent(target, (byte) 35);

        sendSyncPacket(target, newLayers, totalNirvanaLevel, persistentData.getInt("NirvanaRestorationTimer"));

        if (target instanceof ServerPlayer sp) {
            sp.inventoryMenu.broadcastChanges();
        }
    }

    @SubscribeEvent
    public void onNirvanaTickRestoration(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide) return;

        if (!ModMythicConfig.ENABLE_UNDYING_NIRVANA.get()) return;

        if (!isImportantEntity(entity)) return;

        int totalNirvanaLevel = getArmorNirvanaLevel(entity);
        if (totalNirvanaLevel <= 0) return;
        
        CompoundTag persistentData = entity.getPersistentData();
        persistentData.putInt("NirvanaMaxLayers", totalNirvanaLevel);

        if (!persistentData.contains("NirvanaLayers")) {
            persistentData.putInt("NirvanaLayers", totalNirvanaLevel);
        }
        int currentLayers = persistentData.getInt("NirvanaLayers");
        if (currentLayers > totalNirvanaLevel) {
            currentLayers = totalNirvanaLevel;
            persistentData.putInt("NirvanaLayers", currentLayers);
            persistentData.putInt("NirvanaRestorationTimer", 0);
            sendSyncPacket(entity, currentLayers, totalNirvanaLevel, 0);
            return;
        }

        int currentTimer = persistentData.contains("NirvanaRestorationTimer") ? persistentData.getInt("NirvanaRestorationTimer") : 0;

        if (currentLayers < totalNirvanaLevel) {
            double baseSeconds = ModMythicConfig.UNDYING_NIRVANA_FACTOR_B.get().doubleValue();
            
            int ticksNeeded = (int) ((baseSeconds * 20.0D) / (double) Math.max(1, totalNirvanaLevel));
            if (ticksNeeded <= 0) ticksNeeded = 1;

            currentTimer++;

            if (currentTimer >= ticksNeeded) {
                currentLayers += 1;
                currentTimer = 0;
                persistentData.putInt("NirvanaLayers", currentLayers);
                persistentData.putInt("NirvanaRestorationTimer", currentTimer);
                sendSyncPacket(entity, currentLayers, totalNirvanaLevel, currentTimer);
            } else {
                persistentData.putInt("NirvanaRestorationTimer", currentTimer);
                if (entity.tickCount % 20 == 0) {
                    sendSyncPacket(entity, currentLayers, totalNirvanaLevel, currentTimer);
                }
            }
        } else {
            if (currentTimer != 0) {
                persistentData.putInt("NirvanaRestorationTimer", 0);
                sendSyncPacket(entity, currentLayers, totalNirvanaLevel, 0);
            }
        }
    }

    private int getArmorNirvanaLevel(LivingEntity entity) {
        int level = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack armorItem = entity.getItemBySlot(slot);
            if (!armorItem.isEmpty()) {
                level += EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.UNDYING_NIRVANA.get(), armorItem);
            }
        }
        return level;
    }

    private boolean isImportantEntity(LivingEntity entity) {
        return entity instanceof Player || entity.getType().getDescriptionId().contains("maid") || entity.isCustomNameVisible();
    }
}