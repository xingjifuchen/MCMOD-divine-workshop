package com.divineworkshop.mythic;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.PacketDistributor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class ModMythicEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModMythicEventHandler.class);
    private static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("9a41ebda-88bc-4b36-bf31-fa9d300f8da7");
    private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private static final EquipmentSlot[] ALL_ACTIVE_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};

    private static final ThreadLocal<Float> CRIT_MULTIPLIER = ThreadLocal.withInitial(() -> 1.0F);
    private static final ThreadLocal<Boolean> IS_PROCESSING_WEAPON_DAMAGE = ThreadLocal.withInitial(() -> false);

    private static final Map<Integer, Integer> GODLY_BLESSING_COOLDOWN = new HashMap<>();
    private static final Map<Integer, Integer> KILLER_FIELD_COOLDOWN = new HashMap<>();

    public static class PurePlayerThornsSource extends DamageSource {
        private final LivingEntity cleanPlayer;
        public PurePlayerThornsSource(DamageSource originalThornsSource, LivingEntity player) {
            super(originalThornsSource.typeHolder(), null, player, originalThornsSource.getSourcePosition());
            this.cleanPlayer = player;
        }
        @Override
        public Entity getEntity() {
            return this.cleanPlayer instanceof Player p ? p : this.cleanPlayer;
        }
    }

    @SubscribeEvent
    public void onCriticalHit(CriticalHitEvent event) {
        if (event.getDamageModifier() > 1.0F || event.getResult() == Event.Result.ALLOW) {
            CRIT_MULTIPLIER.set(event.getDamageModifier());
        }
    }

    @SubscribeEvent
    public void onOptimizedLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide) return;

        boolean isPlayer = entity instanceof Player;
        boolean isImportantEntity = isPlayer || entity.getType().getDescriptionId().contains("maid") || entity.isCustomNameVisible();
        if (!isImportantEntity) return;

        boolean modEnabled = ModMythicConfig.ENABLE_ETERNAL_IMMORTALITY.get();

        if (modEnabled && isPlayer) {
            Player player = (Player) entity;
            int repairInterval = ModMythicConfig.ETERNAL_IMMORTALITY_TICK_INTERVAL.get();
            if (player.tickCount % repairInterval == 0) {
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    processItemDurabilityAndAttributes(player.getInventory().getItem(i));
                }
            }

            if (player.tickCount % 100 == 0) {
                boolean tagSwitchOn = ModMythicConfig.ENABLE_UNBREAKABLE_TAG.get();
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    ItemStack stack = player.getInventory().getItem(i);
                    if (stack.isEmpty()) continue;

                    int immLevel = EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.ETERNAL_IMMORTALITY.get(), stack);
                    if (immLevel > 0 && tagSwitchOn) {
                        CompoundTag tag = stack.getOrCreateTag();
                        if (!tag.contains("Unbreakable") || !tag.getBoolean("Unbreakable")) {
                            tag.putBoolean("Unbreakable", true);
                        }
                    } else if (tagSwitchOn) {
                        if (stack.hasTag() && stack.getTag().contains("Unbreakable")) {
                            stack.getTag().remove("Unbreakable");
                            if (stack.getTag().isEmpty()) stack.setTag(null);
                        }
                    }
                }
            }
        } else if (modEnabled) {
            int repairInterval = ModMythicConfig.ETERNAL_IMMORTALITY_TICK_INTERVAL.get();
            if (entity.tickCount % repairInterval == 0) {
                for (EquipmentSlot slot : ALL_ACTIVE_SLOTS) {
                    processItemDurabilityAndAttributes(entity.getItemBySlot(slot));
                }
            }
        }

        if (ModMythicConfig.ENABLE_NIRVANA_BREATH.get()) {
            int nirvanaInterval = ModMythicConfig.NIRVANA_TICK_INTERVAL.get();
            if (entity.tickCount % nirvanaInterval == 0) {
                int totalNirvanaLevel = 0;
                for (EquipmentSlot slot : ARMOR_SLOTS) {
                    ItemStack armorItem = entity.getItemBySlot(slot);
                    if (!armorItem.isEmpty()) {
                        totalNirvanaLevel += Math.min(10, EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.NIRVANA_BREATH.get(), armorItem));
                    }
                }
                if (totalNirvanaLevel > 0 && entity.getHealth() < entity.getMaxHealth()) {
                    entity.heal(entity.getMaxHealth() * 0.025F * totalNirvanaLevel);
                }
            }
        }

        if (ModMythicConfig.ENABLE_GODLY_BLESSING.get()) {
            processGodlyBlessing(entity);
        }

        if (ModMythicConfig.ENABLE_KILLER_FIELD.get() && isPlayer) {
            processKillerField((Player) entity);
        }

        if (ModMythicConfig.ENABLE_SKY_WALKER.get() && isPlayer) {
            applySkyWalkerFlight((Player) entity);
        }
    }

    private static void initializeBlessingEffectsIfAbsent(ItemStack stack) {
        if (stack.isEmpty()) return;
        if (EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.GODLY_BLESSING.get(), stack) <= 0) return;
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("BlessingEffects")) return;

        List<String> blacklist = new ArrayList<>(ModMythicConfig.GODLY_BLESSING_BLACKLIST.get());
        List<String> possibleEffects = new ArrayList<>();
        for (var entry : ForgeRegistries.MOB_EFFECTS.getEntries()) {
            String id = entry.getKey().location().toString();
            if (!blacklist.contains(id)) {
                possibleEffects.add(id);
            }
        }
        Random rand = new Random();
        int count = 1 + rand.nextInt(2);
        List<String> selected = new ArrayList<>();
        List<String> pool = new ArrayList<>(possibleEffects);
        for (int i = 0; i < count && !pool.isEmpty(); i++) {
            int idx = rand.nextInt(pool.size());
            selected.add(pool.remove(idx));
        }
        ListTag list = new ListTag();
        for (String id : selected) {
            list.add(StringTag.valueOf(id));
        }
        tag.put("BlessingEffects", list);
    }

    private void processGodlyBlessing(LivingEntity entity) {
        ItemStack chestplate = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (chestplate.isEmpty()) return;

        int level = EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.GODLY_BLESSING.get(), chestplate);
        if (level <= 0) return;

        initializeBlessingEffectsIfAbsent(chestplate);

        if (chestplate.hasTag() && chestplate.getTag().getBoolean("BlessingDisabled")) {
            return;
        }

        CompoundTag tag = chestplate.getTag();
        if (tag == null || !tag.contains("BlessingEffects")) return;

        if (ModMythicConfig.ENABLE_BLESSING_PARTICLES.get() && entity.level() instanceof ServerLevel serverLevel) {
            if (entity.tickCount % 6 == 0) {
                double radius = 2.0 + (2.0 * level);
                int count = Math.max(8, level * 4);
                for (int i = 0; i < count; i++) {
                    double angle = 2 * Math.PI * i / count;
                    double xOffset = radius * Math.cos(angle);
                    double zOffset = radius * Math.sin(angle);
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            entity.getX() + xOffset, entity.getY() + 0.2, entity.getZ() + zOffset,
                            1, 0, 0, 0, 0);
                }
            }
        }

        int refreshSeconds = (level >= 4) ? 10 : (level == 3) ? 15 : (level == 2) ? 20 : 30;
        int refreshTicks = refreshSeconds * 20;
        int entityId = entity.getId();

        int lastTick = GODLY_BLESSING_COOLDOWN.getOrDefault(entityId, 0);
        if (entity.tickCount - lastTick < refreshTicks) return;
        GODLY_BLESSING_COOLDOWN.put(entityId, entity.tickCount);

        List<String> effectIds = tag.getList("BlessingEffects", 8).stream()
                .map(nbt -> nbt.getAsString())
                .toList();

        if (effectIds.isEmpty()) return;

        double radius = 2.0 + (2.0 * level);
        boolean affectOthers = ModMythicConfig.GODLY_BLESSING_AFFECT_OTHERS.get();

        List<LivingEntity> nearbyHostiles = new ArrayList<>();
        if (affectOthers) {
            AABB bounds = entity.getBoundingBox().inflate(radius);
            nearbyHostiles = entity.level().getEntitiesOfClass(LivingEntity.class, bounds, e -> {
                if (!e.isAlive()) return false;
                if (e instanceof Enemy) return true;
                MobCategory category = e.getType().getCategory();
                return category == MobCategory.MONSTER;
            });
        }

        for (String effectId : effectIds) {
            ResourceLocation loc = new ResourceLocation(effectId);
            if (loc == null) continue;
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(loc);
            if (effect == null) continue;

            boolean isBeneficial = effect.isBeneficial();

            if (isBeneficial) {
                applyEffectToTarget(entity, effect, level, refreshSeconds);
            } else if (affectOthers) {
                for (LivingEntity target : nearbyHostiles) {
                    applyEffectToTarget(target, effect, level, refreshSeconds);
                }
            }
        }
    }

    private void applyEffectToTarget(LivingEntity target, MobEffect effect, int level, int refreshSeconds) {
        int finalAmplifier = level - 1;

        if (effect == MobEffects.NIGHT_VISION || effect == MobEffects.INVISIBILITY ||
            effect == MobEffects.GLOWING || effect == MobEffects.WATER_BREATHING) {
            finalAmplifier = 0;
        }

        if (effect == MobEffects.SATURATION) {
            int durationTicks = (refreshSeconds + 5) * 20;
            target.addEffect(new MobEffectInstance(effect, durationTicks, finalAmplifier, false, true));
            return;
        }

        if (effect.isInstantenous()) {
            effect.applyInstantenousEffect(null, null, target, finalAmplifier, 1.0);
        } else {
            int durationTicks = (refreshSeconds + 5) * 20;
            target.addEffect(new MobEffectInstance(effect, durationTicks, finalAmplifier, false, true));
        }
    }

    private void processKillerField(Player player) {
        ItemStack weapon = player.getMainHandItem();
        if (weapon.isEmpty()) return;

        int level = EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.KILLER_FIELD.get(), weapon);
        if (level <= 0) return;

        if (weapon.hasTag() && weapon.getTag().getBoolean("KillerFieldDisabled")) {
            return;
        }

        if (player.level() instanceof ServerLevel serverLevel && player.tickCount % 2 == 0) {
            double radius = 2 + 2 * level;
            int count = Math.max(12, level * 6);
            for (int i = 0; i < count; i++) {
                double angle = 2 * Math.PI * i / count;
                double xOffset = radius * Math.cos(angle);
                double zOffset = radius * Math.sin(angle);
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                        player.getX() + xOffset,
                        player.getY() + 0.2,
                        player.getZ() + zOffset,
                        1, 0, 0, 0, 0);
            }
        }

        int radius = 2 + 2 * level;
        int interval = Math.max(1, 60 / level);
        int entityId = player.getId();

        int lastTick = KILLER_FIELD_COOLDOWN.getOrDefault(entityId, 0);
        if (player.tickCount - lastTick < interval) return;
        KILLER_FIELD_COOLDOWN.put(entityId, player.tickCount);

        AABB bounds = player.getBoundingBox().inflate(radius);
        boolean affectAll = ModMythicConfig.KILLER_FIELD_AFFECT_ALL.get();

        List<LivingEntity> targets = player.level().getEntitiesOfClass(LivingEntity.class, bounds, e -> {
            if (!e.isAlive() || e == player) return false;
            if (affectAll) return true;
            if (e instanceof Enemy) return true;
            MobCategory category = e.getType().getCategory();
            return category == MobCategory.MONSTER;
        });

        if (targets.isEmpty()) return;

        float baseDamage = getWeaponDamage(player, weapon);
        double factor = ModMythicConfig.KILLER_FIELD_BASE.get();
        float damage = baseDamage * 0.0625f * (float) Math.pow(factor, level);

        if (damage <= 0) return;

        for (LivingEntity target : targets) {
            DamageSource damageSource = player.damageSources().playerAttack(player);
            target.hurt(damageSource, damage);
            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                        target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                        1, 0.2, 0.2, 0.2, 0);
            }
        }
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        ItemStack weapon = player.getMainHandItem();
        if (weapon.isEmpty()) return;

        int level = EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.HEAVENLY_SWORD_WILL.get(), weapon);
        if (level <= 0) return;

        int cooldownTicks = Math.max(1, 1000 / level);
        if (player.getCooldowns().isOnCooldown(weapon.getItem())) {
            return;
        }

        int radius = 10 + 20 * level;
        boolean affectAll = ModMythicConfig.HEAVENLY_SWORD_WILL_AFFECT_ALL.get();

        AABB bounds = player.getBoundingBox().inflate(radius);
        List<LivingEntity> targets = player.level().getEntitiesOfClass(LivingEntity.class, bounds, e -> {
            if (!e.isAlive() || e == player) return false;
            if (affectAll) return true;
            if (e instanceof Enemy) return true;
            MobCategory category = e.getType().getCategory();
            return category == MobCategory.MONSTER;
        });

        if (targets.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.divineworkshop.heavenly_sword_will.no_target"));
            return;
        }

        player.getCooldowns().addCooldown(weapon.getItem(), cooldownTicks);

        float baseDamage = getWeaponDamage(player, weapon);
        double factor = ModMythicConfig.HEAVENLY_SWORD_WILL_BASE.get();
        float damage = baseDamage * 4.0f * (float) Math.pow(factor, level);

        if (damage <= 0) return;

        ServerLevel serverLevel = (ServerLevel) player.level();

        Random rand = new Random();
        int endRodCount = 2000 + level * 500;
        for (int i = 0; i < endRodCount; i++) {
            double x = player.getX() + (rand.nextDouble() - 0.5) * radius * 2.2;
            double z = player.getZ() + (rand.nextDouble() - 0.5) * radius * 2.2;
            double y = player.getY() + rand.nextDouble() * 6 - 1;
            serverLevel.sendParticles(ParticleTypes.END_ROD, x, y, z, 0, 0, 0, 0, 1);
        }

        int cloudCount = 800 + level * 200;
        for (int i = 0; i < cloudCount; i++) {
            double x = player.getX() + (rand.nextDouble() - 0.5) * radius * 2.0;
            double z = player.getZ() + (rand.nextDouble() - 0.5) * radius * 2.0;
            double y = player.getY() + rand.nextDouble() * 5 - 0.5;
            serverLevel.sendParticles(ParticleTypes.CLOUD, x, y, z, 0, 0.5, 0.5, 0.5, 1);
        }

        int flashCount = 100 + level * 30;
        for (int i = 0; i < flashCount; i++) {
            double x = player.getX() + (rand.nextDouble() - 0.5) * radius * 1.8;
            double z = player.getZ() + (rand.nextDouble() - 0.5) * radius * 1.8;
            double y = player.getY() + rand.nextDouble() * 4;
            serverLevel.sendParticles(ParticleTypes.FLASH, x, y, z, 0, 0, 0, 0, 1);
        }

        for (LivingEntity target : targets) {
            DamageSource damageSource = player.damageSources().playerAttack(player);
            target.hurt(damageSource, damage);

            LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, serverLevel);
            lightning.setPos(target.getX(), target.getY(), target.getZ());
            lightning.setVisualOnly(true);
            serverLevel.addFreshEntity(lightning);
        }

        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.0F, 1.0F);

        player.sendSystemMessage(Component.translatable(
                "message.divineworkshop.heavenly_sword_will.hit", targets.size()));

        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof Player player)) return;
        if (entity.level().isClientSide) return;

        ItemStack weapon = player.getMainHandItem();
        int level = EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.SOUL_REAVER.get(), weapon);
        if (level <= 0) return;

        double baseCap = ModMythicConfig.SOUL_REAVER_BASE_CAP.get();
        double healthBonus = Math.pow(ModMythicConfig.SOUL_REAVER_BASE_A.get(), level - 1);
        double cap = baseCap * Math.pow(ModMythicConfig.SOUL_REAVER_BASE_B.get(), level - 1);

        CompoundTag persistentData = player.getPersistentData();
        double currentHealthBonus = persistentData.getDouble("SoulReaverHealthBonus");
        double currentAttackBonus = persistentData.getDouble("SoulReaverAttackBonus");

        if (currentHealthBonus >= cap) {
            LOGGER.info("[噬魂夺魄] 玩家 {} 已达到上限 ({}), 不再成长", player.getName().getString(), cap);
            return;
        }

        double newHealthBonus = Math.min(currentHealthBonus + healthBonus, cap);
        double newAttackBonus = Math.min(currentAttackBonus + healthBonus, cap);

        persistentData.putDouble("SoulReaverHealthBonus", newHealthBonus);
        persistentData.putDouble("SoulReaverAttackBonus", newAttackBonus);

        AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);

        if (healthAttr != null) {
            healthAttr.removeModifier(UndyingNirvanaHandler.SOUL_REAVER_HEALTH_UUID);
            if (newHealthBonus > 0) {
                healthAttr.addPermanentModifier(new AttributeModifier(UndyingNirvanaHandler.SOUL_REAVER_HEALTH_UUID,
                        "Soul Reaver Health Bonus", newHealthBonus, AttributeModifier.Operation.ADDITION));
            }
        }
        if (attackAttr != null) {
            attackAttr.removeModifier(UndyingNirvanaHandler.SOUL_REAVER_ATTACK_UUID);
            if (newAttackBonus > 0) {
                attackAttr.addPermanentModifier(new AttributeModifier(UndyingNirvanaHandler.SOUL_REAVER_ATTACK_UUID,
                        "Soul Reaver Attack Bonus", newAttackBonus, AttributeModifier.Operation.ADDITION));
            }
        }

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }

        LOGGER.info("[噬魂夺魄] 玩家 {} 击杀 {}，成长: 生命 +{} -> {}/{}, 攻击 +{} -> {}/{}, cap={}",
                player.getName().getString(),
                entity.getType().getDescriptionId(),
                healthBonus,
                newHealthBonus, cap,
                healthBonus,
                newAttackBonus, cap,
                cap);

        if (player instanceof ServerPlayer serverPlayer) {
            ModMythicNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new SoulReaverSyncPacket(player.getId(), newHealthBonus, newAttackBonus, cap));
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        applySkyWalkerFlight(event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        applySkyWalkerFlight(event.getEntity());
    }

    @SubscribeEvent
    public void onEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null) return;

        if (ModMythicConfig.ENABLE_ETERNAL_IMMORTALITY.get() && ModMythicConfig.ENABLE_INSTANT_REPAIR.get()) {
            ItemStack toStack = event.getTo();
            if (!toStack.isEmpty() && toStack.isDamageableItem()) {
                if (EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.ETERNAL_IMMORTALITY.get(), toStack) > 0) toStack.setDamageValue(0);
            }
        }

        AttributeInstance maxHealthAttr = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr != null) {
            maxHealthAttr.removeModifier(HEALTH_MODIFIER_UUID);
            if (ModMythicConfig.ENABLE_TITAN_GIFT.get()) {
                int totalTitanLevel = 0;
                for (EquipmentSlot slot : ARMOR_SLOTS) {
                    ItemStack armorItem = entity.getItemBySlot(slot);
                    if (!armorItem.isEmpty()) totalTitanLevel += EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.TITAN_GIFT.get(), armorItem);
                }
                if (totalTitanLevel > 0) {
                    double baseFactor = ModMythicConfig.TITAN_GIFT_FACTOR.get();
                    double multiplierValue = Math.pow(baseFactor, totalTitanLevel - 1) - 1.0;
                    maxHealthAttr.addPermanentModifier(new AttributeModifier(HEALTH_MODIFIER_UUID, "Titan's Gift Health", multiplierValue, AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
            }
            if (entity.getHealth() > entity.getMaxHealth()) entity.setHealth(entity.getMaxHealth());
        }

        if (entity instanceof Player player) {
            if (event.getSlot() == EquipmentSlot.FEET) {
                applySkyWalkerFlight(player);
            }
        }
    }

    private void applySkyWalkerFlight(Player player) {
        if (player.level().isClientSide) return;
        
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        boolean hasSkyWalker = ModMythicConfig.ENABLE_SKY_WALKER.get() && 
            EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.SKY_WALKER.get(), boots) > 0;
        
        CompoundTag tag = player.getPersistentData();
        boolean grantedBySkyWalker = tag.getBoolean("PE_SkyWalkerFlight");

        if (hasSkyWalker) {
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }
            if (!grantedBySkyWalker) {
                tag.putBoolean("PE_SkyWalkerFlight", true);
            }
        } else {
            if (grantedBySkyWalker) {
                tag.remove("PE_SkyWalkerFlight");
                if (!player.isCreative() && !player.isSpectator()) {
                    boolean finalWallFlightActive = com.divineworkshop.finalending.ModFinalEndingEventHandler.hasFullSetFinalWall(player) 
                        && com.divineworkshop.finalending.ModFinalEndingConfig.d_creative_flight;
                    if (!finalWallFlightActive) {
                        player.getAbilities().mayfly = false;
                        player.getAbilities().flying = false;
                        player.onUpdateAbilities();
                    }
                }
            }
        }
    }

    private float getWeaponDamage(Player player, ItemStack weapon) {
        AttributeInstance attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        float baseDamage = (float) attackAttr.getValue();

        int sharpness = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SHARPNESS, weapon);
        if (sharpness > 0) {
            baseDamage += 1.0f + 0.5f * (sharpness - 1);
        }

        int smite = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SMITE, weapon);
        if (smite > 0) {
            baseDamage += 2.5f * smite;
        }
        int bane = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BANE_OF_ARTHROPODS, weapon);
        if (bane > 0) {
            baseDamage += 2.5f * bane;
        }

        return Math.max(baseDamage, 1.0f);
    }

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (left.isEmpty() || right.isEmpty()) return;

        if (!ModMythicConfig.ENABLE_GODLY_BLESSING.get()) return;

        int leftLevel = getBlessingLevelFromTag(left);
        int rightLevel = getBlessingLevelFromTag(right);

        if (rightLevel <= 0) return;

        int finalLevel = leftLevel;
        if (leftLevel == rightLevel) {
            finalLevel = Math.min(ModMythicConfig.GODLY_BLESSING_MAX_LEVEL.get(), leftLevel + 1);
        } else if (rightLevel > leftLevel) {
            finalLevel = rightLevel;
        } else if (leftLevel == 0) {
            finalLevel = rightLevel;
        }

        Map<Enchantment, Integer> leftEnchants = new HashMap<>(EnchantmentHelper.getEnchantments(left));
        Map<Enchantment, Integer> rightEnchants = new HashMap<>(EnchantmentHelper.getEnchantments(right));

        Map<Enchantment, Integer> mergedEnchants = new HashMap<>(leftEnchants);
        boolean isLeftBook = left.is(Items.ENCHANTED_BOOK);
        for (Map.Entry<Enchantment, Integer> entry : rightEnchants.entrySet()) {
            Enchantment ench = entry.getKey();
            int rightLvl = entry.getValue();
            if (isLeftBook || ench.canEnchant(left)) {
                if (mergedEnchants.containsKey(ench)) {
                    int leftLvl = mergedEnchants.get(ench);
                    if (leftLvl == rightLvl) {
                        mergedEnchants.put(ench, Math.min(ench.getMaxLevel(), leftLvl + 1));
                    } else {
                        mergedEnchants.put(ench, Math.max(leftLvl, rightLvl));
                    }
                } else {
                    mergedEnchants.put(ench, rightLvl);
                }
            }
        }

        Enchantment godlyBlessing = ModMythicEnchantments.GODLY_BLESSING.get();
        mergedEnchants.put(godlyBlessing, finalLevel);

        ItemStack output = left.copy();
        output.setCount(1);
        EnchantmentHelper.setEnchantments(mergedEnchants, output);

        boolean leftHas = left.hasTag() && left.getTag().contains("BlessingEffects");
        boolean rightHas = right.hasTag() && right.getTag().contains("BlessingEffects");
        CompoundTag outTag = output.getOrCreateTag();

        if (leftHas && rightHas) {
            Set<String> mergedEffects = new LinkedHashSet<>();
            extractEffectsFromItem(left, mergedEffects);
            extractEffectsFromItem(right, mergedEffects);
            ListTag mergedList = new ListTag();
            for (String id : mergedEffects) {
                mergedList.add(StringTag.valueOf(id));
            }
            outTag.put("BlessingEffects", mergedList);
        } else if (rightHas) {
            outTag.put("BlessingEffects", right.getTag().getList("BlessingEffects", 8).copy());
        } else if (leftHas) {
            outTag.put("BlessingEffects", left.getTag().getList("BlessingEffects", 8).copy());
        } else {
            if (mergedEnchants.containsKey(godlyBlessing)) {
                initializeBlessingEffectsIfAbsent(output);
            }
        }

        if (left.hasTag() && left.getTag().contains("BlessingDisabled")) {
            outTag.putBoolean("BlessingDisabled", left.getTag().getBoolean("BlessingDisabled"));
        }

        int cost = finalLevel * 5;
        int leftRepairCost = left.hasTag() ? left.getTag().getInt("RepairCost") : 0;
        int rightRepairCost = right.hasTag() ? right.getTag().getInt("RepairCost") : 0;
        cost += leftRepairCost + rightRepairCost;
        if (cost <= 0) cost = 1;
        outTag.putInt("RepairCost", Math.max(leftRepairCost, rightRepairCost) * 2 + 1);

        event.setOutput(output);
        event.setCost(cost);
        event.setMaterialCost(1);
    }

    @SubscribeEvent
    public void onEntityItemPickup(EntityItemPickupEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof Player)) return;
        ItemStack stack = event.getItem().getItem();
        if (!stack.isEmpty() && EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.GODLY_BLESSING.get(), stack) > 0) {
            initializeBlessingEffectsIfAbsent(stack);
        }
    }

    private int getBlessingLevelFromTag(ItemStack stack) {
        if (!stack.hasTag()) return 0;
        CompoundTag tag = stack.getTag();
        String targetId = ForgeRegistries.ENCHANTMENTS.getKey(ModMythicEnchantments.GODLY_BLESSING.get()).toString();
        int lvl = checkTagListForEnch(tag.getList("Enchantments", 10), targetId);
        if (lvl > 0) return lvl;
        return checkTagListForEnch(tag.getList("StoredEnchantments", 10), targetId);
    }

    private int checkTagListForEnch(ListTag list, String targetId) {
        for (int i = 0; i < list.size(); i++) {
            CompoundTag subTag = list.getCompound(i);
            if (subTag.getString("id").equals(targetId)) {
                return subTag.getInt("lvl");
            }
        }
        return 0;
    }

    private void extractEffectsFromItem(ItemStack stack, Set<String> set) {
        if (stack.hasTag() && stack.getTag().contains("BlessingEffects")) {
            ListTag list = stack.getTag().getList("BlessingEffects", 8);
            for (int i = 0; i < list.size(); i++) {
                set.add(list.getString(i));
            }
        }
    }

    private void processItemDurabilityAndAttributes(ItemStack stack) {
        if (stack.isEmpty()) return;
        int immLevel = EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.ETERNAL_IMMORTALITY.get(), stack);
        boolean unbreakingSwitchOn = ModMythicConfig.ENABLE_VANILLA_UNBREAKING_255.get();

        if (immLevel > 0) {
            if (stack.isDamageableItem() && stack.isDamaged()) stack.setDamageValue(0);
            if (unbreakingSwitchOn) {
                int currentUnbreaking = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
                if (currentUnbreaking != 255) {
                    Map<Enchantment, Integer> enchants = new HashMap<>(EnchantmentHelper.getEnchantments(stack));
                    enchants.put(Enchantments.UNBREAKING, 255);
                    EnchantmentHelper.setEnchantments(enchants, stack);
                }
            }
        } else {
            if (unbreakingSwitchOn) cleanUpVanillaUnbreaking255(stack);
        }
    }

    private void cleanUpVanillaUnbreaking255(ItemStack stack) {
        if (!stack.hasTag() || (!stack.getTag().contains("Enchantments") && !stack.getTag().contains("StoredEnchantments"))) return;
        int currentUnbreaking = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
        if (currentUnbreaking == 255) {
            Map<Enchantment, Integer> enchants = new HashMap<>(EnchantmentHelper.getEnchantments(stack));
            enchants.remove(Enchantments.UNBREAKING);
            EnchantmentHelper.setEnchantments(enchants, stack);
        }
    }

    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!ModMythicConfig.ENABLE_ETERNAL_IMMORTALITY.get()) return;
        ItemStack stack = event.getCrafting();
        if (stack != null && !stack.isEmpty() && stack.isDamageableItem()) {
            if (EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.ETERNAL_IMMORTALITY.get(), stack) > 0) stack.setDamageValue(0);
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null) return;

        if (ModMythicConfig.ENABLE_ETERNAL_IMMORTALITY.get() && ModMythicConfig.ENABLE_INSTANT_REPAIR.get()) {
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                ItemStack armor = entity.getItemBySlot(slot);
                if (!armor.isEmpty() && EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.ETERNAL_IMMORTALITY.get(), armor) > 0) armor.setDamageValue(0);
            }
        }

        if (ModMythicConfig.ENABLE_IRON_WALL.get() && ModMythicConfig.REDUCE_VOID_DAMAGE.get() && event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD)) {
            if (entity.invulnerableTime > 0) { event.setCanceled(true); return; }
            int totalProtectionLevel = 0;
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                ItemStack armorItem = entity.getItemBySlot(slot);
                if (!armorItem.isEmpty()) totalProtectionLevel += EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.IRON_WALL.get(), armorItem);
            }
            if (totalProtectionLevel > 0) {
                event.setCanceled(true); entity.invulnerableTime = 20;
                entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_HURT, SoundSource.NEUTRAL, 1.0F, 1.0F);
                entity.level().broadcastEntityEvent(entity, (byte) 2);
                entity.setHealth(Math.max(0.0F, entity.getHealth() - (event.getAmount() * (float) Math.pow(ModMythicConfig.IRON_WALL_FACTOR.get(), totalProtectionLevel))));
                return;
            }
        }

        if (ModMythicConfig.ENABLE_BACKLASH_THORN.get() && event.getSource().getEntity() instanceof LivingEntity attacker) {
            int totalThornLevel = 0;
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                ItemStack armorItem = entity.getItemBySlot(slot);
                if (!armorItem.isEmpty()) totalThornLevel += EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.BACKLASH_THORN.get(), armorItem);
            }
            if (totalThornLevel > 0) {
                float retaliatedDamage = event.getAmount() * (float) (Math.pow(ModMythicConfig.BACKLASH_THORN_FACTOR.get(), totalThornLevel) - 1.0);
                if (retaliatedDamage > 0.05F) {
                    DamageSource baseThorns = entity.level().damageSources().thorns(entity);
                    PurePlayerThornsSource pureSource = new PurePlayerThornsSource(baseThorns, entity);
                    attacker.hurt(pureSource, retaliatedDamage);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD)) return;

        LivingEntity target = event.getEntity();
        if (target == null) return;

        if (ModMythicConfig.ENABLE_ETERNAL_IMMORTALITY.get() && ModMythicConfig.ENABLE_INSTANT_REPAIR.get()) {
            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                ItemStack mainHand = attacker.getMainHandItem();
                if (!mainHand.isEmpty() && EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.ETERNAL_IMMORTALITY.get(), mainHand) > 0) mainHand.setDamageValue(0);
            }
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                ItemStack armor = target.getItemBySlot(slot);
                if (!armor.isEmpty() && EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.ETERNAL_IMMORTALITY.get(), armor) > 0) armor.setDamageValue(0);
            }
        }

        if (IS_PROCESSING_WEAPON_DAMAGE.get()) {
            proceedIronWallProtection(event, target);
            CRIT_MULTIPLIER.set(1.0F);
            return;
        }

        if (event.getSource() instanceof PurePlayerThornsSource && !ModMythicConfig.BACKLASH_THORN_PARTICIPATE_MULTIPLIER.get()) {
            proceedIronWallProtection(event, target);
            CRIT_MULTIPLIER.set(1.0F);
            return;
        }

        float pendingFlatBloodSurgeDamage = 0.0F;

        IS_PROCESSING_WEAPON_DAMAGE.set(true);
        try {
            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                ItemStack weapon = attacker.getMainHandItem();
                if (!weapon.isEmpty()) {

                    if (ModMythicConfig.ENABLE_BLOOD_SURGE.get()) {
                        int surgeLevel = EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.BLOOD_SURGE.get(), weapon);
                        if (surgeLevel > 0) {
                            double factor = ModMythicConfig.BLOOD_SURGE_FACTOR.get();
                            float attackerMaxHealth = attacker.getMaxHealth();
                            float additionalDamage = attackerMaxHealth * (float) (Math.pow(factor, surgeLevel) - 1.0);

                            if (ModMythicConfig.BLOOD_SURGE_AS_BASE_DAMAGE.get()) {
                                float currentCritMultiplier = CRIT_MULTIPLIER.get();
                                if (currentCritMultiplier > 1.0F) {
                                    additionalDamage *= currentCritMultiplier;
                                }
                                event.setAmount(event.getAmount() + additionalDamage);
                            } else {
                                pendingFlatBloodSurgeDamage = additionalDamage;
                            }
                        }
                    }

                    if (ModMythicConfig.ENABLE_VANQUISH.get()) {
                        int vanquishLevel = EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.VANQUISH.get(), weapon);
                        if (vanquishLevel > 0) {
                            event.setAmount(event.getAmount() * (float) Math.pow(ModMythicConfig.VANQUISH_FACTOR.get(), vanquishLevel));
                        }
                    }
                }
            }

            if (pendingFlatBloodSurgeDamage > 0.0F) {
                event.setAmount(event.getAmount() + pendingFlatBloodSurgeDamage);
            }

        } finally {
            IS_PROCESSING_WEAPON_DAMAGE.set(false);
        }

        proceedIronWallProtection(event, target);
        CRIT_MULTIPLIER.set(1.0F);
    }

    private void proceedIronWallProtection(LivingHurtEvent event, LivingEntity target) {
        if (ModMythicConfig.ENABLE_IRON_WALL.get() && target != null) {
            int totalIronWallLevel = 0;
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                ItemStack armorItem = target.getItemBySlot(slot);
                if (!armorItem.isEmpty()) totalIronWallLevel += EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.IRON_WALL.get(), armorItem);
            }
            if (totalIronWallLevel > 0) {
                float originalAmount = event.getAmount();
                float newAmount = originalAmount * (float) Math.pow(ModMythicConfig.IRON_WALL_FACTOR.get(), totalIronWallLevel);
                event.setAmount(newAmount);
                if (newAmount < 1.0F && originalAmount > 0) event.setAmount(Math.max(0.1F, newAmount));
            }
        }
    }

    @SubscribeEvent
    public void onWardenDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Warden && ModMythicConfig.ENABLE_BLOOD_SURGE.get()) {
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantedBookItem.addEnchantment(book, new EnchantmentInstance(ModMythicEnchantments.BLOOD_SURGE.get(), 1));

            net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
                    entity.level(), entity.getX(), entity.getY(), entity.getZ(), book
            );
            event.getDrops().add(itemEntity);
        }
    }
}