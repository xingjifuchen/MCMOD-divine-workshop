package com.divineworkshop.finalending;

import com.divineworkshop.finalending.network.C2SRangeAttackPacket;
import com.divineworkshop.finalending.network.ModFinalEndingNetwork;
import com.divineworkshop.mythic.ModMythicEnchantments;
import com.divineworkshop.mythic.ModMythicConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "divineworkshop", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModFinalEndingEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModFinalEndingEventHandler.class);

    private static final ThreadLocal<Boolean> IS_PROCESSING_KILL = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Boolean> IS_PROCESSING_REVIVE = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Boolean> IS_PROCESSING_DISARM = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Boolean> IS_PROCESSING_REFLECT = ThreadLocal.withInitial(() -> false);

    private static final Set<UUID> DOOMED_ENTITIES = ConcurrentHashMap.newKeySet();

    private static final Map<UUID, Map<Attribute, Double>> ORIGINAL_ATTRIBUTES = new ConcurrentHashMap<>();
    private static final List<Attribute> ALL_ATTRIBUTES = List.of(
            Attributes.MAX_HEALTH,
            Attributes.ARMOR,
            Attributes.ARMOR_TOUGHNESS,
            Attributes.ATTACK_DAMAGE,
            Attributes.ATTACK_SPEED,
            Attributes.MOVEMENT_SPEED,
            Attributes.KNOCKBACK_RESISTANCE,
            Attributes.LUCK,
            Attributes.FLYING_SPEED,
            Attributes.ATTACK_KNOCKBACK,
            Attributes.FOLLOW_RANGE
    );

    public static boolean hasFullSetFinalWall(LivingEntity entity) {
        if (entity == null) return false;
        ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = entity.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = entity.getItemBySlot(EquipmentSlot.FEET);
        return EnchantmentHelper.getItemEnchantmentLevel(ModFinalEndingEnchantments.FINAL_WALL.get(), head) > 0 &&
               EnchantmentHelper.getItemEnchantmentLevel(ModFinalEndingEnchantments.FINAL_WALL.get(), chest) > 0 &&
               EnchantmentHelper.getItemEnchantmentLevel(ModFinalEndingEnchantments.FINAL_WALL.get(), legs) > 0 &&
               EnchantmentHelper.getItemEnchantmentLevel(ModFinalEndingEnchantments.FINAL_WALL.get(), feet) > 0;
    }

    private static boolean attackerHasFinalJudge(LivingEntity attacker) {
        if (attacker == null) return false;
        ItemStack weapon = attacker.getMainHandItem();
        return EnchantmentHelper.getItemEnchantmentLevel(ModFinalEndingEnchantments.FINAL_JUDGE.get(), weapon) > 0;
    }

    private static boolean hasAnyFinalEnchantment(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return EnchantmentHelper.getItemEnchantmentLevel(ModFinalEndingEnchantments.FINAL_JUDGE.get(), stack) > 0 ||
               EnchantmentHelper.getItemEnchantmentLevel(ModFinalEndingEnchantments.FINAL_WALL.get(), stack) > 0;
    }

    private static void killByNbtRewrite(LivingEntity entity) {
        if (entity == null) return;
        if (entity instanceof Player) {
            entity.setHealth(0.0F);
            entity.die(entity.level().damageSources().genericKill());
            return;
        }
        try {
            CompoundTag nbt = new CompoundTag();
            entity.saveWithoutId(nbt);
            nbt.putFloat("Health", 0.0F);

            if (nbt.contains("Attributes", 9)) {
                ListTag attributes = nbt.getList("Attributes", 10);
                for (int i = 0; i < attributes.size(); i++) {
                    CompoundTag attr = attributes.getCompound(i);
                    if ("minecraft:generic.max_health".equals(attr.getString("Name"))) {
                        attr.putDouble("Base", 0.0D);
                    }
                }
            }

            entity.load(nbt);
            entity.setHealth(0.0F);
            entity.die(entity.level().damageSources().genericKill());
            entity.deathTime = 999999;
        } catch (Exception e) {
            LOGGER.error("NBT 物理改血失败", e);
            try {
                java.lang.reflect.Field healthField = LivingEntity.class.getDeclaredField("health");
                healthField.setAccessible(true);
                healthField.setFloat(entity, 0.0F);
                entity.die(entity.level().damageSources().genericKill());
                entity.deathTime = 999999;
            } catch (Exception ex) {
                entity.setHealth(0.0F);
                entity.die(entity.level().damageSources().genericKill());
            }
        }
    }

    public static void performRangeAttack(ServerPlayer player) {
        if (player == null) return;
        ItemStack held = player.getMainHandItem();
        if (!attackerHasFinalJudge(player)) return;

        ServerLevel level = player.serverLevel();
        Vec3 center = player.position();
        float range = ModFinalEndingConfig.ATTACK_RANGE;
        boolean isGlobal = range < 0;

        if (ModFinalEndingConfig.loot_erase_drops || ModFinalEndingConfig.loot_erase_exp) {
            Iterator<Entity> iterator = level.getAllEntities().iterator();
            int itemPurged = 0, orbPurged = 0;
            while (iterator.hasNext()) {
                Entity e = iterator.next();
                if (!isGlobal && e.distanceToSqr(center) > range * range) continue;
                if (e instanceof ItemEntity && ModFinalEndingConfig.loot_erase_drops) {
                    e.discard();
                    itemPurged++;
                } else if (e instanceof ExperienceOrb && ModFinalEndingConfig.loot_erase_exp) {
                    e.discard();
                    orbPurged++;
                }
            }
            if (itemPurged > 0 || orbPurged > 0) {
                player.sendSystemMessage(Component.translatable("final_ending.message.cleanup", itemPurged, orbPurged));
            }
        }

        if (ModFinalEndingConfig.clear_all_entities) {
            int entityPurged = 0;
            Iterator<Entity> iterator = level.getAllEntities().iterator();
            while (iterator.hasNext()) {
                Entity e = iterator.next();
                if (e == player) continue;
                if (!isGlobal && e.distanceToSqr(center) > range * range) continue;
                if (e instanceof Player) continue;
                e.discard();
                entityPurged++;
            }
            iterator = level.getAllEntities().iterator();
            while (iterator.hasNext()) {
                Entity e = iterator.next();
                if (e == player) continue;
                if (!isGlobal && e.distanceToSqr(center) > range * range) continue;
                if (e instanceof Player) continue;
                e.discard();
                entityPurged++;
            }
            if (entityPurged > 0) {
                player.sendSystemMessage(Component.translatable("final_ending.message.clear_entities", entityPurged));
            }
        }

        List<LivingEntity> targets = new ArrayList<>();
        if (isGlobal) {
            for (Entity e : level.getAllEntities()) {
                if (e instanceof LivingEntity && e != player) {
                    targets.add((LivingEntity) e);
                }
            }
        } else {
            AABB box = new AABB(center.x - range, center.y - range, center.z - range,
                                center.x + range, center.y + range, center.z + range);
            targets = level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player);
        }

        int totalTargets = targets.size();
        for (LivingEntity target : targets) {
            executePipelineKill(player, target, level);
        }

        if (totalTargets > 0) {
            boolean isErase = ModFinalEndingConfig.m_set_removed || ModFinalEndingConfig.m_discard;
            if (isErase) {
                player.sendSystemMessage(Component.translatable("final_ending.message.judge_erase", totalTargets));
            } else {
                player.sendSystemMessage(Component.translatable("final_ending.message.judge_attack", totalTargets));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickItem event) {
        if (event.getLevel().isClientSide()) return;
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (!attackerHasFinalJudge(player)) return;
        performRangeAttack(player);
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!ModFinalEndingConfig.d_prevent_spawning) return;
        if (event.getEntity() instanceof Player) return;
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttackEntity(AttackEntityEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!attackerHasFinalJudge(player)) return;
        if (!(event.getTarget() instanceof LivingEntity target)) return;

        if (!ModFinalEndingConfig.d_attack_feedback) {
            event.setCanceled(true);
        }
        ServerLevel level = (ServerLevel) player.level();
        executePipelineKill(player, target, level);
    }

    private static void executePipelineKill(LivingEntity attacker, LivingEntity target, ServerLevel level) {
        if (IS_PROCESSING_KILL.get()) return;
        IS_PROCESSING_KILL.set(true);
        try {
            Vec3 pos = target.position();
            ModFinalEndingEffects.playTargetDeathVisuals(level, pos, attacker);

            if (ModFinalEndingConfig.loot_force_drop) {
                spawnLootFromEntity(target, level, attacker);
                int exp = target.getExperienceReward();
                if (exp > 0) {
                    ExperienceOrb orb = new ExperienceOrb(level, pos.x, pos.y + 0.5, pos.z, exp);
                    level.addFreshEntity(orb);
                }
            }

            if (attacker != null) {
                target.setLastHurtByMob(attacker);
            }

            if (ModFinalEndingConfig.m_disarm) {
                clearAllInventory(target);
            }

            if (ModFinalEndingConfig.loot_erase_drops) {
                target.captureDrops(new ArrayList<>());
            }

            float amt = ModFinalEndingConfig.DAMAGE_AMOUNT;
            DamageSource source = level.damageSources().mobAttack(attacker);
            for (String type : ModFinalEndingConfig.ACTIVE_DAMAGE_TYPES) {
                target.hurt(source, amt);
                target.invulnerableTime = 0;
                target.hurtTime = 0;
            }

            if (ModFinalEndingConfig.m_set_health_zero) target.setHealth(0.0F);
            if (ModFinalEndingConfig.m_call_die) target.die(level.damageSources().genericKill());
            if (ModFinalEndingConfig.m_lock_death_time) target.deathTime = 999999;
            if (ModFinalEndingConfig.m_set_removed) target.setRemoved(Entity.RemovalReason.KILLED);
            if (ModFinalEndingConfig.m_discard) target.discard();

            if (ModFinalEndingConfig.m_set_max_health_zero) {
                AttributeInstance maxHealthAttr = target.getAttribute(Attributes.MAX_HEALTH);
                if (maxHealthAttr != null) {
                    maxHealthAttr.setBaseValue(0.0);
                }
                DOOMED_ENTITIES.add(target.getUUID());
                target.setHealth(0.0F);
                target.die(level.damageSources().genericKill());
            }

            if (ModFinalEndingConfig.m_set_max_health_zero_v2) {
                killByNbtRewrite(target);
                DOOMED_ENTITIES.add(target.getUUID());
            }

        } finally {
            IS_PROCESSING_KILL.set(false);
        }
    }

    private static void spawnLootFromEntity(LivingEntity entity, ServerLevel level, LivingEntity killer) {
        if (entity == null || level == null) return;
        ResourceLocation lootTableId = entity.getLootTable();
        if (lootTableId == null || lootTableId.equals(ResourceLocation.withDefaultNamespace("empty"))) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = entity.getItemBySlot(slot);
                if (!stack.isEmpty()) {
                    entity.spawnAtLocation(stack);
                    entity.setItemSlot(slot, ItemStack.EMPTY);
                }
            }
            ItemStack mainHand = entity.getMainHandItem();
            if (!mainHand.isEmpty()) {
                entity.spawnAtLocation(mainHand);
                entity.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
            ItemStack offHand = entity.getOffhandItem();
            if (!offHand.isEmpty()) {
                entity.spawnAtLocation(offHand);
                entity.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }
            return;
        }

        LootTable lootTable = level.getServer().getLootData().getLootTable(lootTableId);
        if (lootTable == null) return;

        LootParams.Builder paramBuilder = new LootParams.Builder(level);
        paramBuilder.withParameter(LootContextParams.THIS_ENTITY, entity);
        paramBuilder.withParameter(LootContextParams.ORIGIN, entity.position());
        DamageSource source = killer != null ? level.damageSources().mobAttack(killer) : level.damageSources().genericKill();
        paramBuilder.withParameter(LootContextParams.DAMAGE_SOURCE, source);
        if (killer != null) {
            paramBuilder.withOptionalParameter(LootContextParams.KILLER_ENTITY, killer);
            paramBuilder.withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, killer);
        }
        LootParams params = paramBuilder.create(LootContextParamSets.ENTITY);
        List<ItemStack> drops = lootTable.getRandomItems(params);
        for (ItemStack drop : drops) {
            entity.spawnAtLocation(drop);
        }
    }

    private static void clearAllInventory(LivingEntity target) {
        if (target instanceof Player player) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                player.getInventory().setItem(i, ItemStack.EMPTY);
            }
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            target.setItemSlot(slot, ItemStack.EMPTY);
        }
        target.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        target.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!ModFinalEndingConfig.d_prevent_disarm) return;
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (IS_PROCESSING_DISARM.get()) return;

        EquipmentSlot slot = event.getSlot();
        ItemStack oldStack = event.getFrom();
        ItemStack newStack = event.getTo();

        if (!hasAnyFinalEnchantment(oldStack)) return;
        if (!slot.isArmor() && slot != EquipmentSlot.OFFHAND && slot != EquipmentSlot.MAINHAND) return;

        boolean isRealRemove = false;
        if (newStack.isEmpty()) {
            isRealRemove = true;
        } else {
            boolean stillInInventory = false;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (ItemStack.isSameItem(oldStack, stack) && ItemStack.isSameItemSameTags(oldStack, stack)) {
                    stillInInventory = true;
                    break;
                }
            }
            isRealRemove = !stillInInventory;
        }

        if (isRealRemove) {
            IS_PROCESSING_DISARM.set(true);
            try {
                entity.setItemSlot(slot, oldStack.copy());
                if (!newStack.isEmpty()) {
                    if (!player.getInventory().add(newStack)) {
                        player.drop(newStack, false);
                    }
                }
                player.displayClientMessage(Component.translatable("final_ending.message.protect_item"), true);
            } finally {
                IS_PROCESSING_DISARM.set(false);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onItemToss(ItemTossEvent event) {
        if (!ModFinalEndingConfig.d_prevent_disarm) return;
        Player player = event.getPlayer();
        ItemStack tossed = event.getEntity().getItem();
        if (hasAnyFinalEnchantment(tossed)) {
            event.setCanceled(true);
            player.displayClientMessage(Component.translatable("final_ending.message.protect_item"), true);
        }
    }

    public static void injectEternalDurability(MinecraftServer server) {
        if (server == null) return;
        int totalInjected = 0;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            totalInjected += applyEternalDurabilityToPlayer(player);
        }
        if (totalInjected > 0) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.sendSystemMessage(Component.translatable("final_ending.message.inject_eternal", totalInjected));
            }
        } else {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.sendSystemMessage(Component.translatable("final_ending.message.inject_none"));
            }
        }
    }

    private static int applyEternalDurabilityToPlayer(ServerPlayer player) {
        int count = 0;
        Enchantment eternalImmortality = ModMythicEnchantments.ETERNAL_IMMORTALITY.get();
        if (eternalImmortality == null) return 0;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && hasAnyFinalEnchantment(stack)) {
                if (addEternalImmortalityEnchant(stack, eternalImmortality)) {
                    player.getInventory().setItem(i, stack);
                    count++;
                }
            }
        }
        for (int i = 0; i < player.getInventory().armor.size(); i++) {
            ItemStack stack = player.getInventory().armor.get(i);
            if (!stack.isEmpty() && hasAnyFinalEnchantment(stack)) {
                if (addEternalImmortalityEnchant(stack, eternalImmortality)) {
                    player.getInventory().armor.set(i, stack);
                    count++;
                }
            }
        }
        ItemStack offhand = player.getInventory().offhand.get(0);
        if (!offhand.isEmpty() && hasAnyFinalEnchantment(offhand)) {
            if (addEternalImmortalityEnchant(offhand, eternalImmortality)) {
                player.getInventory().offhand.set(0, offhand);
                count++;
            }
        }
        if (count > 0) {
            player.containerMenu.broadcastChanges();
        }
        return count;
    }

    private static boolean addEternalImmortalityEnchant(ItemStack stack, Enchantment enchant) {
        if (stack.isEmpty() || enchant == null) return false;
        if (EnchantmentHelper.getItemEnchantmentLevel(enchant, stack) > 0) return false;
        stack.enchant(enchant, 1);
        return EnchantmentHelper.getItemEnchantmentLevel(enchant, stack) > 0;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        if (!hasFullSetFinalWall(entity)) return;
        if (ModFinalEndingConfig.d_cancel_attack) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (!hasFullSetFinalWall(entity)) return;

        if (ModFinalEndingConfig.d_force_heal) {
            event.setAmount(0.0F);
            entity.setHealth(entity.getMaxHealth());
        }
        if (ModFinalEndingConfig.d_cancel_hurt) {
            event.setAmount(0.0F);
            if (ModFinalEndingConfig.d_force_heal) {
                entity.setHealth(entity.getMaxHealth());
            }
        }
        if (ModFinalEndingConfig.d_cancel_damage) {
            event.setAmount(0.0F);
            if (ModFinalEndingConfig.d_force_heal) {
                entity.setHealth(entity.getMaxHealth());
            }
        }

        if (ModFinalEndingConfig.d_reflect_damage && !IS_PROCESSING_REFLECT.get()) {
            DamageSource source = event.getSource();
            Entity attacker = source.getEntity();
            if (attacker instanceof LivingEntity livingAttacker && livingAttacker != entity) {
                IS_PROCESSING_REFLECT.set(true);
                try {
                    float damage = ModFinalEndingConfig.DAMAGE_AMOUNT;
                    DamageSource reflectSource = entity.level().damageSources().mobAttack(entity);
                    livingAttacker.hurt(reflectSource, damage);
                } finally {
                    IS_PROCESSING_REFLECT.set(false);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (!hasFullSetFinalWall(entity)) return;
        if (ModFinalEndingConfig.d_cancel_damage) {
            event.setCanceled(true);
            if (ModFinalEndingConfig.d_force_heal) {
                entity.setHealth(entity.getMaxHealth());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!hasFullSetFinalWall(entity)) return;

        if (ModFinalEndingConfig.d_cancel_death) {
            event.setCanceled(true);
            entity.deathTime = 0;
            entity.setHealth(entity.getMaxHealth());
            entity.revive();
            entity.invulnerableTime = 40;
            return;
        }

        if (ModFinalEndingConfig.d_revive_on_death) {
            if (IS_PROCESSING_REVIVE.get()) return;
            IS_PROCESSING_REVIVE.set(true);
            try {
                entity.deathTime = 0;
                entity.setHealth(entity.getMaxHealth());
                entity.revive();
                entity.invulnerableTime = 40;
                if (event.isCancelable()) {
                    event.setCanceled(true);
                }
            } finally {
                IS_PROCESSING_REVIVE.set(false);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onKnockback(LivingKnockBackEvent event) {
        LivingEntity entity = event.getEntity();
        if (!hasFullSetFinalWall(entity)) return;
        if (ModFinalEndingConfig.d_cancel_knockback) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDimensionTravel(EntityTravelToDimensionEvent event) {
        if (event.getEntity() instanceof LivingEntity entity && hasFullSetFinalWall(entity)) {
            if (ModFinalEndingConfig.d_cancel_dimension) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();
        if (!hasFullSetFinalWall(entity)) return;
        MobEffectInstance inst = event.getEffectInstance();
        if (inst == null) return;

        String effectKey = BuiltInRegistries.MOB_EFFECT.getKey(inst.getEffect()).toString();
        boolean isWhitelisted = ModFinalEndingConfig.ACTIVE_EFFECTS.contains(effectKey);

        if (isWhitelisted) return;

        if (ModFinalEndingConfig.d_remove_all_effects) {
            event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
            return;
        }

        if (ModFinalEndingConfig.d_remove_negative_effects) {
            if (!inst.getEffect().isBeneficial()) {
                event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide()) return;

        if (!DOOMED_ENTITIES.isEmpty()) {
            UUID uuid = entity.getUUID();
            if (DOOMED_ENTITIES.contains(uuid)) {
                if (!entity.isAlive() || entity.isRemoved()) {
                    DOOMED_ENTITIES.remove(uuid);
                } else {
                    entity.setHealth(0.0F);
                    entity.die(entity.level().damageSources().genericKill());
                    entity.deathTime = 999999;
                    LivingEntity attacker = entity.getLastHurtByMob();
                    if (attacker != null) {
                        DamageSource physical = entity.level().damageSources().mobAttack(attacker);
                        entity.hurt(physical, Float.MAX_VALUE / 2);
                        DamageSource voidDmg = entity.level().damageSources().fellOutOfWorld();
                        entity.hurt(voidDmg, Float.MAX_VALUE / 2);
                    } else {
                        entity.hurt(entity.level().damageSources().genericKill(), Float.MAX_VALUE / 2);
                    }
                    if (entity.isAlive()) {
                        killByNbtRewrite(entity);
                    }
                    if (entity.isRemoved()) {
                        DOOMED_ENTITIES.remove(uuid);
                    }
                }
            }
        }

        if (entity instanceof ServerPlayer player) {
            boolean hasWall = hasFullSetFinalWall(player);
            boolean finalWallFlightActive = hasWall && ModFinalEndingConfig.d_creative_flight;
            
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
            boolean hasSkyWalker = ModMythicConfig.ENABLE_SKY_WALKER.get() && 
                EnchantmentHelper.getItemEnchantmentLevel(ModMythicEnchantments.SKY_WALKER.get(), boots) > 0;
            
            CompoundTag tag = player.getPersistentData();
            boolean grantedByFinalWall = tag.getBoolean("PE_FinalWallFlight");

            if (finalWallFlightActive) {
                if (!player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = true;
                    player.onUpdateAbilities();
                }
                if (!grantedByFinalWall) {
                    tag.putBoolean("PE_FinalWallFlight", true);
                }
            } else {
                if (grantedByFinalWall) {
                    tag.remove("PE_FinalWallFlight");
                    if (!player.isCreative() && !player.isSpectator()) {
                        if (!hasSkyWalker) {
                            player.getAbilities().mayfly = false;
                            player.getAbilities().flying = false;
                            player.onUpdateAbilities();
                        }
                    }
                }
            }
        }

        boolean hasWall = hasFullSetFinalWall(entity);
        if (!hasWall) {
            cleanupAttributes(entity);
            return;
        }

        if (ModFinalEndingConfig.d_lock_max_health) {
            AttributeInstance maxHealthAttr = entity.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttr != null && maxHealthAttr.getBaseValue() != 20.0) {
                maxHealthAttr.setBaseValue(20.0);
                if (entity.getHealth() > entity.getMaxHealth()) {
                    entity.setHealth(entity.getMaxHealth());
                }
            }
        }

        if (ModFinalEndingConfig.d_tick_force_heal) {
            entity.setHealth(entity.getMaxHealth());
            if (entity.deathTime > 0) entity.deathTime = 0;
        }

        if (ModFinalEndingConfig.d_invulnerable) {
            entity.setInvulnerable(true);
            entity.hurtTime = 0;
        } else {
            if (!(entity instanceof ServerPlayer player && player.isCreative())) {
                entity.setInvulnerable(false);
            }
        }

        if (ModFinalEndingConfig.d_revive_on_death) {
            if (entity.isDeadOrDying() || entity.deathTime > 0) {
                entity.deathTime = 0;
                entity.revive();
                entity.setHealth(entity.getMaxHealth());
                entity.invulnerableTime = 40;
            }
        }

        if (ModFinalEndingConfig.d_remove_all_effects) {
            List<MobEffectInstance> effects = List.copyOf(entity.getActiveEffects());
            for (MobEffectInstance instance : effects) {
                String effectKey = BuiltInRegistries.MOB_EFFECT.getKey(instance.getEffect()).toString();
                if (!ModFinalEndingConfig.ACTIVE_EFFECTS.contains(effectKey)) {
                    entity.removeEffect(instance.getEffect());
                }
            }
        } else if (ModFinalEndingConfig.d_remove_negative_effects) {
            List<MobEffectInstance> effects = List.copyOf(entity.getActiveEffects());
            for (MobEffectInstance instance : effects) {
                String effectKey = BuiltInRegistries.MOB_EFFECT.getKey(instance.getEffect()).toString();
                if (!ModFinalEndingConfig.ACTIVE_EFFECTS.contains(effectKey) && !instance.getEffect().isBeneficial()) {
                    entity.removeEffect(instance.getEffect());
                }
            }
        }

        applyEffects(entity);
        applyAttributes(entity);
    }

    private static void applyEffects(LivingEntity entity) {
        for (String effectName : ModFinalEndingConfig.ACTIVE_EFFECTS) {
            try {
                ResourceLocation rl = new ResourceLocation(effectName);
                if (rl == null) continue;
                MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(rl);
                if (effect == null) continue;
                int level = ModFinalEndingConfig.EFFECT_LEVELS.getOrDefault(effectName, 0);
                level = Math.max(0, Math.min(255, level));
                MobEffectInstance inst = new MobEffectInstance(effect, 40, level, false, false, true);
                MobEffectInstance existing = entity.getEffect(effect);
                if (existing == null || existing.getAmplifier() != level) {
                    entity.addEffect(inst);
                }
            } catch (Exception ignored) {}
        }
    }

    private static void applyAttributes(LivingEntity entity) {
        UUID uuid = entity.getUUID();

        if (ModFinalEndingConfig.d_attribute_modify) {
            if (!ORIGINAL_ATTRIBUTES.containsKey(uuid)) {
                Map<Attribute, Double> orig = new HashMap<>();
                for (Attribute attr : ALL_ATTRIBUTES) {
                    AttributeInstance inst = entity.getAttribute(attr);
                    if (inst != null) {
                        orig.put(attr, inst.getBaseValue());
                    }
                }
                if (!orig.isEmpty()) {
                    ORIGINAL_ATTRIBUTES.put(uuid, orig);
                }
            }

            setAttributeValue(entity, Attributes.MAX_HEALTH, getConfigAttrValue("minecraft:generic.max_health"));
            setAttributeValue(entity, Attributes.ARMOR, getConfigAttrValue("minecraft:generic.armor"));
            setAttributeValue(entity, Attributes.ARMOR_TOUGHNESS, getConfigAttrValue("minecraft:generic.armor_toughness"));
            setAttributeValue(entity, Attributes.ATTACK_DAMAGE, getConfigAttrValue("minecraft:generic.attack_damage"));
            setAttributeValue(entity, Attributes.ATTACK_SPEED, getConfigAttrValue("minecraft:generic.attack_speed"));
            setAttributeValue(entity, Attributes.MOVEMENT_SPEED, getConfigAttrValue("minecraft:generic.movement_speed"));
            setAttributeValue(entity, Attributes.KNOCKBACK_RESISTANCE, getConfigAttrValue("minecraft:generic.knockback_resistance"));
            setAttributeValue(entity, Attributes.LUCK, getConfigAttrValue("minecraft:generic.luck"));
            setAttributeValue(entity, Attributes.FLYING_SPEED, getConfigAttrValue("minecraft:generic.flying_speed"));
            setAttributeValue(entity, Attributes.ATTACK_KNOCKBACK, getConfigAttrValue("minecraft:generic.attack_knockback"));
            setAttributeValue(entity, Attributes.FOLLOW_RANGE, getConfigAttrValue("minecraft:generic.follow_range"));

            if (entity.getHealth() > entity.getMaxHealth()) {
                entity.setHealth(entity.getMaxHealth());
            }

        } else {
            cleanupAttributes(entity);
        }
    }

    private static double getConfigAttrValue(String key) {
        return ModFinalEndingConfig.attr_values.getOrDefault(key, 0.0);
    }

    private static void setAttributeValue(LivingEntity entity, Attribute attr, double value) {
        AttributeInstance inst = entity.getAttribute(attr);
        if (inst != null) {
            inst.setBaseValue(value);
        }
    }

    private static void cleanupAttributes(LivingEntity entity) {
        UUID uuid = entity.getUUID();
        Map<Attribute, Double> orig = ORIGINAL_ATTRIBUTES.remove(uuid);
        if (orig != null) {
            for (Map.Entry<Attribute, Double> entry : orig.entrySet()) {
                AttributeInstance inst = entity.getAttribute(entry.getKey());
                if (inst != null) {
                    inst.setBaseValue(entry.getValue());
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = "divineworkshop", value = Dist.CLIENT)
    public static class ClientHandler {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            if (!ModFinalEndingConfig.d_auto_attack) return;
            var player = net.minecraft.client.Minecraft.getInstance().player;
            if (player == null) return;
            ModFinalEndingNetwork.INSTANCE.sendToServer(new C2SRangeAttackPacket());
        }
    }
}