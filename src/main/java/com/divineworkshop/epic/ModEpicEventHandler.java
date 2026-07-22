package com.divineworkshop.epic;

import com.divineworkshop.normal.ModNormalConfig;
import com.divineworkshop.normal.ModNormalEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "divineworkshop")
public class ModEpicEventHandler {

    private static final Random RANDOM = new Random();

    private static final Map<UUID, Integer> LURKING_TIMER = new ConcurrentHashMap<>();

    public static int getLurkingTicks(UUID uuid) {
        return LURKING_TIMER.getOrDefault(uuid, 0);
    }

    @SubscribeEvent
    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        Entity attacker = event.getAttackingPlayer();
        if (!(attacker instanceof Player player)) return;
        if (player.level().isClientSide) return;

        ItemStack mainHand = player.getMainHandItem();
        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEpicEnchantments.UNIVERSE_RECEIVER.get(), mainHand);
        if (level > 0 && ModEpicConfig.ENABLE_UNIVERSE_RECEIVER.get()) {
            int multiplier = (int) Math.pow(2, level);
            int originalExp = event.getDroppedExperience();
            if (originalExp > 0) {
                event.setDroppedExperience(originalExp * multiplier);
            }
        }
    }

    private static boolean hasSilkTouch(ItemStack tool) {
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0) {
            return true;
        }
        if (ModNormalConfig.ENABLE_SIMPLE_SILK_TOUCH.get()) {
            if (EnchantmentHelper.getItemEnchantmentLevel(ModNormalEnchantments.PRECIOUS_HARVEST.get(), tool) > 0) {
                CompoundTag tag = tool.getTag();
                boolean enabled = (tag == null || !tag.contains("NormalEnchantEnabled")) || tag.getBoolean("NormalEnchantEnabled");
                if (enabled) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isCropMature(BlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.NETHER_WART) {
            if (state.hasProperty(NetherWartBlock.AGE)) {
                int age = state.getValue(NetherWartBlock.AGE);
                return age >= NetherWartBlock.MAX_AGE;
            }
            return false;
        }
        for (Property<?> property : state.getProperties()) {
            if (property.getName().equals("age") && property instanceof IntegerProperty intProp) {
                int current = state.getValue(intProp);
                int max = intProp.getPossibleValues().stream().max(Integer::compare).orElse(0);
                return current >= max;
            }
        }
        return true;
    }

    private static boolean isFortuneApplicable(BlockState state) {
        if (state.is(Tags.Blocks.ORES)) {
            String blockName = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
            if (blockName.equals("minecraft:ancient_debris")) {
                return false;
            }
            return true;
        }
        if (state.is(BlockTags.CROPS)) {
            return isCropMature(state);
        }
        String blockName = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        switch (blockName) {
            case "minecraft:gravel":
            case "minecraft:melon":
            case "minecraft:pumpkin":
            case "minecraft:sweet_berry_bush":
            case "minecraft:cave_vines":
            case "minecraft:cave_vines_plant":
                return true;
            case "minecraft:nether_wart":
                return isCropMature(state);
            default:
                if (blockName.startsWith("minecraft:leaves") || blockName.startsWith("minecraft:azalea_leaves")) {
                    return true;
                }
                return false;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null || player.level().isClientSide) return;

        ItemStack mainHand = player.getMainHandItem();
        int universeLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEpicEnchantments.UNIVERSE_RECEIVER.get(), mainHand);
        if (universeLevel <= 0 || !ModEpicConfig.ENABLE_UNIVERSE_RECEIVER.get()) {
            return;
        }

        if (hasSilkTouch(mainHand)) {
            return;
        }

        BlockPos pos = event.getPos();
        ServerLevel world = (ServerLevel) player.level();
        BlockState state = event.getState();

        if (!isFortuneApplicable(state)) {
            return;
        }

        int baseMultiplier = (int) Math.pow(2, universeLevel);
        int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, mainHand);
        int finalMultiplier = baseMultiplier;
        if (ModEpicConfig.COMPATIBLE_WITH_FORTUNE.get() && fortuneLevel > 0) {
            finalMultiplier *= (int) Math.pow(2, fortuneLevel);
        }

        event.setCanceled(true);

        int originalExp = event.getExpToDrop();
        if (originalExp > 0) {
            int expToDrop = originalExp * finalMultiplier;
            world.addFreshEntity(new ExperienceOrb(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, expToDrop));
        }

        List<ItemStack> drops = Block.getDrops(state, world, pos, null, player, mainHand);
        if (!drops.isEmpty()) {
            for (ItemStack drop : drops) {
                int newCount = Math.min(drop.getMaxStackSize(), drop.getCount() * finalMultiplier);
                if (newCount > 0) {
                    ItemStack finalDrop = drop.copy();
                    finalDrop.setCount(newCount);
                    Block.popResource(world, pos, finalDrop);
                }
            }
        }

        world.removeBlock(pos, false);
        world.levelEvent(player, 2001, pos, Block.getId(state));
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        Entity killer = event.getSource().getEntity();
        if (!(killer instanceof Player player) || player.level().isClientSide) return;

        ItemStack mainHand = player.getMainHandItem();
        int universeLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEpicEnchantments.UNIVERSE_RECEIVER.get(), mainHand);
        if (universeLevel <= 0 || !ModEpicConfig.ENABLE_UNIVERSE_RECEIVER.get()) return;

        int baseMultiplier = (int) Math.pow(2, universeLevel);
        int lootingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, mainHand);

        int finalMultiplier = baseMultiplier;
        if (ModEpicConfig.COMPATIBLE_WITH_LOOTING.get() && lootingLevel > 0) {
            finalMultiplier *= (lootingLevel + 1);
        }

        if (event.getEntity() instanceof WitherSkeleton && universeLevel >= 3) {
            boolean hasSkull = false;
            for (ItemEntity itemEntity : event.getDrops()) {
                if (itemEntity.getItem().is(Items.WITHER_SKELETON_SKULL)) {
                    hasSkull = true;
                    break;
                }
            }
            if (!hasSkull) {
                ItemStack skull = new ItemStack(Items.WITHER_SKELETON_SKULL, 1);
                ItemEntity skullEntity = new ItemEntity(player.level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), skull);
                event.getDrops().add(skullEntity);
            }
        }

        Collection<ItemEntity> drops = event.getDrops();
        for (ItemEntity itemEntity : drops) {
            ItemStack stack = itemEntity.getItem();
            if (finalMultiplier > 1) {
                int newCount = Math.min(stack.getMaxStackSize(), stack.getCount() * finalMultiplier);
                stack.setCount(newCount);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtCurse(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source.getEntity() instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.getMainHandItem();
            if (!weapon.isEmpty() && ModEpicConfig.ENABLE_CURSE_EROSION.get()) {
                int erosionLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEpicEnchantments.CURSE_EROSION.get(), weapon);
                if (erosionLevel > 0) {
                    LivingEntity target = event.getEntity();
                    if (target != null && target.isAlive()) {
                        int durationInTicks = (5 + (erosionLevel - 1) * 5) * 20;
                        int amplifier = erosionLevel - 1;

                        for (String effectId : ModEpicConfig.CURSE_EROSION_EFFECTS.get()) {
                            ResourceLocation loc = new ResourceLocation(effectId);
                            if (loc != null) {
                                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(loc);
                                if (effect != null) {
                                    target.addEffect(new MobEffectInstance(effect, durationInTicks, amplifier));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBloodReturn(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source.getEntity() instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.getMainHandItem();
            if (!weapon.isEmpty() && ModEpicConfig.ENABLE_BLOOD_RETURN.get()) {
                int level = EnchantmentHelper.getItemEnchantmentLevel(ModEpicEnchantments.BLOOD_RETURN.get(), weapon);
                if (level > 0) {
                    LivingEntity target = event.getEntity();
                    float targetMaxHealth = target.getMaxHealth();
                    double a = ModEpicConfig.BLOOD_RETURN_BASE_A.get();
                    double b = ModEpicConfig.BLOOD_RETURN_BASE_B.get();
                    double healPercent = 0.125 * Math.pow(a, level);
                    double absorptionPercent = 0.03125 * Math.pow(b, level);

                    int healAmount = (int) (targetMaxHealth * healPercent);
                    int absorptionAmount = (int) (targetMaxHealth * absorptionPercent);

                    if (healAmount > 0) attacker.heal(healAmount);
                    if (absorptionAmount > 0) {
                        attacker.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, absorptionAmount / 4, false, false, true));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLastStand(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source.getEntity() instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.getMainHandItem();
            if (!weapon.isEmpty() && ModEpicConfig.ENABLE_LAST_STAND.get()) {
                int level = EnchantmentHelper.getItemEnchantmentLevel(ModEpicEnchantments.LAST_STAND.get(), weapon);
                if (level > 0) {
                    float healthRatio = attacker.getHealth() / attacker.getMaxHealth();
                    double baseMultiplier;
                    if (healthRatio <= 0.05) baseMultiplier = 3.0;
                    else if (healthRatio <= 0.20) baseMultiplier = 1.0;
                    else if (healthRatio <= 0.40) baseMultiplier = 0.5;
                    else if (healthRatio <= 0.60) baseMultiplier = 0.3;
                    else if (healthRatio <= 0.80) baseMultiplier = 0.1;
                    else baseMultiplier = 0.0;

                    if (baseMultiplier > 0) {
                        double a = ModEpicConfig.LAST_STAND_BASE_A.get();
                        double finalMultiplier = 1.0 + baseMultiplier * Math.pow(a, level - 1);
                        float originalDamage = event.getAmount();
                        event.setAmount(originalDamage * (float) finalMultiplier);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLurkingStrike(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source.getEntity() instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.getMainHandItem();
            if (!weapon.isEmpty() && ModEpicConfig.ENABLE_LURKING_STRIKE.get()) {
                int level = EnchantmentHelper.getItemEnchantmentLevel(ModEpicEnchantments.LURKING_STRIKE.get(), weapon);
                if (level > 0) {
                    UUID attackerId = attacker.getUUID();
                    int ticks = LURKING_TIMER.getOrDefault(attackerId, 0);
                    if (ticks > 0) {
                        double a = ModEpicConfig.LURKING_STRIKE_BASE_A.get();
                        double b = ModEpicConfig.LURKING_STRIKE_BASE_B.get();
                        double multiplier = 1.0 + a * ticks * Math.pow(b, level);
                        float originalDamage = event.getAmount();
                        event.setAmount(originalDamage * (float) multiplier);
                    }
                    LURKING_TIMER.put(attackerId, 0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            var server = event.getServer();
            if (server != null) {
                for (var level : server.getAllLevels()) {
                    for (var entity : level.getAllEntities()) {
                        if (entity instanceof LivingEntity living && living.isAlive()) {
                            ItemStack weapon = living.getMainHandItem();
                            if (!weapon.isEmpty()) {
                                int levelEnchant = EnchantmentHelper.getItemEnchantmentLevel(ModEpicEnchantments.LURKING_STRIKE.get(), weapon);
                                if (levelEnchant > 0 && ModEpicConfig.ENABLE_LURKING_STRIKE.get()) {
                                    UUID id = living.getUUID();
                                    LURKING_TIMER.put(id, LURKING_TIMER.getOrDefault(id, 0) + 1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static final UUID SWIFT_STRIKES_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef");
    private static final String SWIFT_STRIKES_MODIFIER_NAME = "SwiftStrikesSpeedBoost";

    @SubscribeEvent
    public static void onLivingTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        var server = event.getServer();
        if (server == null) return;

        for (var level : server.getAllLevels()) {
            for (var entity : level.getAllEntities()) {
                if (entity instanceof LivingEntity living && living.isAlive()) {
                    ItemStack mainHand = living.getMainHandItem();
                    if (mainHand.isEmpty()) continue;

                    int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEpicEnchantments.SWIFT_STRIKES.get(), mainHand);
                    if (enchantLevel > 0 && ModEpicConfig.ENABLE_SWIFT_STRIKES.get()) {
                        var attribute = living.getAttribute(Attributes.ATTACK_SPEED);
                        if (attribute != null) {
                            boolean hasModifier = attribute.getModifier(SWIFT_STRIKES_UUID) != null;
                            double speedBonus = 0.2 * enchantLevel;
                            if (!hasModifier) {
                                AttributeModifier modifier = new AttributeModifier(
                                        SWIFT_STRIKES_UUID,
                                        SWIFT_STRIKES_MODIFIER_NAME,
                                        speedBonus,
                                        AttributeModifier.Operation.ADDITION
                                );
                                attribute.addPermanentModifier(modifier);
                            } else {
                                AttributeModifier existing = attribute.getModifier(SWIFT_STRIKES_UUID);
                                if (existing != null && Math.abs(existing.getAmount() - speedBonus) > 0.001) {
                                    attribute.removeModifier(SWIFT_STRIKES_UUID);
                                    AttributeModifier newMod = new AttributeModifier(
                                            SWIFT_STRIKES_UUID,
                                            SWIFT_STRIKES_MODIFIER_NAME,
                                            speedBonus,
                                            AttributeModifier.Operation.ADDITION
                                    );
                                    attribute.addPermanentModifier(newMod);
                                }
                            }
                        }
                    } else {
                        var attribute = living.getAttribute(Attributes.ATTACK_SPEED);
                        if (attribute != null && attribute.getModifier(SWIFT_STRIKES_UUID) != null) {
                            attribute.removeModifier(SWIFT_STRIKES_UUID);
                        }
                    }
                }
            }
        }
    }
}