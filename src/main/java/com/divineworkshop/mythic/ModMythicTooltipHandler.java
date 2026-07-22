package com.divineworkshop.mythic;

import com.divineworkshop.client.ModTooltipSorterDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class ModMythicTooltipHandler {

    private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public static boolean hasMythic(Map<Enchantment, Integer> enchantments) {
        for (Enchantment enchantment : enchantments.keySet()) {
            if (isMythicEnchantment(enchantment)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isMythicEnchantment(Enchantment e) {
        return e == ModMythicEnchantments.ETERNAL_IMMORTALITY.get() ||
               e == ModMythicEnchantments.BACKLASH_THORN.get() ||
               e == ModMythicEnchantments.NIRVANA_BREATH.get() ||
               e == ModMythicEnchantments.TITAN_GIFT.get() ||
               e == ModMythicEnchantments.IRON_WALL.get() ||
               e == ModMythicEnchantments.VANQUISH.get() ||
               e == ModMythicEnchantments.BLOOD_SURGE.get() ||
               e == ModMythicEnchantments.UNDYING_NIRVANA.get() ||
               e == ModMythicEnchantments.GODLY_BLESSING.get() ||
               e == ModMythicEnchantments.KILLER_FIELD.get() ||
               e == ModMythicEnchantments.HEAVENLY_SWORD_WILL.get() ||
               e == ModMythicEnchantments.SOUL_REAVER.get() ||
               e == ModMythicEnchantments.SKY_WALKER.get();
    }

    public static void collectNodes(ItemTooltipEvent event, Map<Enchantment, Integer> enchantments, ItemStack itemStack, List<ModTooltipSorterDispatcher.DisplayNode> nodes) {
        LivingEntity entity = event.getEntity();
        if (Minecraft.getInstance().hitResult instanceof net.minecraft.world.phys.EntityHitResult entityHitResult) {
            if (entityHitResult.getEntity() instanceof LivingEntity livingTarget) {
                entity = livingTarget;
            }
        }

        boolean isEquipped = false;
        if (entity != null) {
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                if (entity.getItemBySlot(slot) == itemStack) {
                    isEquipped = true;
                    break;
                }
            }
            if (!isEquipped && entity.getMainHandItem() == itemStack) {
                isEquipped = true;
            }
            if (!isEquipped && entity.getOffhandItem() == itemStack) {
                isEquipped = true;
            }
        }

        String statusOn = Component.translatable("tooltip.divineworkshop.status_enabled").getString();
        String statusOff = Component.translatable("tooltip.divineworkshop.status_disabled").getString();

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();

            if (!isMythicEnchantment(enchantment)) continue;

            List<Component> lines = new ArrayList<>();

            Component levelComp = Component.literal("[LV ")
                    .append(Component.literal(String.valueOf(level)).withStyle(ChatFormatting.GOLD))
                    .append(Component.literal("]").withStyle(ChatFormatting.GOLD))
                    .withStyle(ChatFormatting.GOLD);

            if (enchantment == ModMythicEnchantments.ETERNAL_IMMORTALITY.get()) {
                boolean instantRepair = ModMythicConfig.ENABLE_INSTANT_REPAIR.get();
                boolean unbreakableTag = ModMythicConfig.ENABLE_UNBREAKABLE_TAG.get();
                boolean unbreaking255 = ModMythicConfig.ENABLE_VANILLA_UNBREAKING_255.get();
                double repairSeconds = ModMythicConfig.ETERNAL_IMMORTALITY_TICK_INTERVAL.get() / 20.0;

                lines.add(Component.translatable("tooltip.divineworkshop.eternal_immortality.line1", levelComp));
                lines.add(Component.translatable("tooltip.divineworkshop.eternal_immortality.line2", (int)repairSeconds));
                lines.add(Component.translatable("tooltip.divineworkshop.eternal_immortality.line3", instantRepair ? statusOn : statusOff));
                lines.add(Component.translatable("tooltip.divineworkshop.eternal_immortality.line4", unbreakableTag ? statusOn : statusOff));
                lines.add(Component.translatable("tooltip.divineworkshop.eternal_immortality.line5", unbreaking255 ? statusOn : statusOff));
            }
            else if (enchantment == ModMythicEnchantments.VANQUISH.get()) {
                double factor = ModMythicConfig.VANQUISH_FACTOR.get();
                double times = Math.pow(factor, level);
                String factorStr = String.format("%.2f", factor);
                String timesStr = String.format("%.2f", times);
                lines.add(Component.translatable("tooltip.divineworkshop.vanquish.line1", levelComp));
                lines.add(Component.translatable("tooltip.divineworkshop.vanquish.line2", factorStr, timesStr));
            }
            else if (enchantment == ModMythicEnchantments.IRON_WALL.get()) {
                double factor = ModMythicConfig.IRON_WALL_FACTOR.get();
                double result = (1.0 - Math.pow(factor, level)) * 100;
                String factorStr = String.format("%.2f", factor);
                String resultStr = String.format("%.2f", result);
                lines.add(Component.translatable("tooltip.divineworkshop.iron_wall.line1", levelComp));
                lines.add(Component.translatable("tooltip.divineworkshop.iron_wall.line2", factorStr, resultStr));
                if (isEquipped && entity != null) {
                    int total = getArmorTotalLevel(entity, ModMythicEnchantments.IRON_WALL.get());
                    double totalRes = (1.0 - Math.pow(factor, total)) * 100;
                    String totalResStr = String.format("%.2f", totalRes);
                    lines.add(Component.translatable("tooltip.divineworkshop.iron_wall.line3", total, totalResStr));
                }
            }
            else if (enchantment == ModMythicEnchantments.TITAN_GIFT.get()) {
                double factor = ModMythicConfig.TITAN_GIFT_FACTOR.get();
                double result = level > 0 ? Math.pow(factor, level - 1) * 100 : 100.0;
                String factorStr = String.format("%.2f", factor);
                String resultStr = String.format("%.2f", result);
                lines.add(Component.translatable("tooltip.divineworkshop.titan_gift.line1", levelComp));
                lines.add(Component.translatable("tooltip.divineworkshop.titan_gift.line2", factorStr, resultStr));
                if (isEquipped && entity != null) {
                    int total = getArmorTotalLevel(entity, ModMythicEnchantments.TITAN_GIFT.get());
                    double totalRes = total > 0 ? Math.pow(factor, total - 1) * 100 : 100.0;
                    String totalResStr = String.format("%.2f", totalRes);
                    lines.add(Component.translatable("tooltip.divineworkshop.titan_gift.line3", total, totalResStr));
                }
            }
            else if (enchantment == ModMythicEnchantments.NIRVANA_BREATH.get()) {
                double interval = ModMythicConfig.NIRVANA_TICK_INTERVAL.get() / 20.0;
                double result = 2.5 * level;
                String resultStr = String.format("%.2f", result);
                lines.add(Component.translatable("tooltip.divineworkshop.nirvana_breath.line1", levelComp));
                lines.add(Component.translatable("tooltip.divineworkshop.nirvana_breath.line2", (int)interval, resultStr));
                if (isEquipped && entity != null) {
                    int total = getArmorTotalLevel(entity, ModMythicEnchantments.NIRVANA_BREATH.get());
                    double totalRes = 2.5 * total;
                    String totalResStr = String.format("%.2f", totalRes);
                    lines.add(Component.translatable("tooltip.divineworkshop.nirvana_breath.line3", total, totalResStr));
                }
            }
            else if (enchantment == ModMythicEnchantments.BACKLASH_THORN.get()) {
                double factor = ModMythicConfig.BACKLASH_THORN_FACTOR.get();
                double result = (Math.pow(factor, level) - 1.0) * 100;
                String factorStr = String.format("%.2f", factor);
                String resultStr = String.format("%.2f", result);
                lines.add(Component.translatable("tooltip.divineworkshop.backlash_thorn.line1", levelComp));
                lines.add(Component.translatable("tooltip.divineworkshop.backlash_thorn.line2", factorStr, resultStr));
                if (isEquipped && entity != null) {
                    int total = getArmorTotalLevel(entity, ModMythicEnchantments.BACKLASH_THORN.get());
                    double totalRes = (Math.pow(factor, total) - 1.0) * 100;
                    String totalResStr = String.format("%.2f", totalRes);
                    lines.add(Component.translatable("tooltip.divineworkshop.backlash_thorn.line3", total, totalResStr));
                }
            }
            else if (enchantment == ModMythicEnchantments.BLOOD_SURGE.get()) {
                double factor = ModMythicConfig.BLOOD_SURGE_FACTOR.get();
                float currentMaxHealth = (entity != null) ? entity.getMaxHealth() : 20.0F;
                float bonus = currentMaxHealth * (float) (Math.pow(factor, level) - 1.0);
                String factorStr = String.format("%.2f", factor);
                String bonusStr = String.format("%.2f", bonus);
                String modeStr = Component.translatable(
                    ModMythicConfig.BLOOD_SURGE_AS_BASE_DAMAGE.get() ? 
                        "tooltip.divineworkshop.blood_surge.mode_base" : 
                        "tooltip.divineworkshop.blood_surge.mode_flat"
                ).getString();
                lines.add(Component.translatable("tooltip.divineworkshop.blood_surge.line1", levelComp));
                lines.add(Component.translatable("tooltip.divineworkshop.blood_surge.line2", factorStr, modeStr));
                lines.add(Component.translatable("tooltip.divineworkshop.blood_surge.line3", currentMaxHealth, bonusStr));
            }
            else if (enchantment == ModMythicEnchantments.UNDYING_NIRVANA.get()) {
                double a = ModMythicConfig.UNDYING_NIRVANA_FACTOR_A.get() * 100.0;
                double b = ModMythicConfig.UNDYING_NIRVANA_FACTOR_B.get();
                double c = ModMythicConfig.UNDYING_NIRVANA_FACTOR_C.get();

                String aStr = String.format("%.2f", a);
                String bStr = String.format("%.1f", b);
                String cStr = String.format("%.1f", c);

                lines.add(Component.translatable("tooltip.divineworkshop.undying_nirvana.line1", levelComp));
                lines.add(Component.translatable("tooltip.divineworkshop.undying_nirvana.line2", aStr));
                lines.add(Component.translatable("tooltip.divineworkshop.undying_nirvana.line3", bStr, cStr));

                if (entity != null) {
                    int total = getArmorTotalLevel(entity, ModMythicEnchantments.UNDYING_NIRVANA.get());
                    CompoundTag tag = entity.getPersistentData();
                    int currentLayers = tag.contains("NirvanaLayers") ? tag.getInt("NirvanaLayers") : total;
                    int timer = tag.contains("NirvanaRestorationTimer") ? tag.getInt("NirvanaRestorationTimer") : 0;

                    lines.add(Component.translatable("tooltip.divineworkshop.undying_nirvana.line4", total, currentLayers, total));
                    if (currentLayers < total) {
                        int ticksNeeded = (int) ((b * 20.0D) / (double) Math.max(1, total));
                        double remainingSecs = Math.max(0.0, (ticksNeeded - timer) / 20.0);
                        lines.add(Component.translatable("tooltip.divineworkshop.undying_nirvana.line5", (int)remainingSecs));
                    } else {
                        lines.add(Component.translatable("tooltip.divineworkshop.undying_nirvana.full"));
                    }
                } else {
                    lines.add(Component.translatable("tooltip.divineworkshop.undying_nirvana.line4", "?", "?", "?"));
                }
            }
            else if (enchantment == ModMythicEnchantments.GODLY_BLESSING.get()) {
                lines.add(Component.translatable("tooltip.divineworkshop.godly_blessing.line1", levelComp));

                int refreshSec;
                if (level >= 4) refreshSec = 10;
                else if (level == 3) refreshSec = 15;
                else if (level == 2) refreshSec = 20;
                else refreshSec = 30;
                int radius = 2 + 2 * level;
                lines.add(Component.translatable("tooltip.divineworkshop.godly_blessing.line2", refreshSec, radius));

                CompoundTag tag = itemStack.getTag();
                if (tag != null && tag.contains("BlessingEffects")) {
                    ListTag list = tag.getList("BlessingEffects", 8);
                    if (!list.isEmpty()) {
                        MutableComponent effectsText = Component.literal("  ").append(Component.translatable("tooltip.divineworkshop.godly_blessing.effects").withStyle(ChatFormatting.GRAY));
                        for (int i = 0; i < list.size(); i++) {
                            String id = list.getString(i);
                            ResourceLocation loc = new ResourceLocation(id);
                            if (loc != null) {
                                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(loc);
                                String displayName = effect != null ? Component.translatable(effect.getDescriptionId()).getString() : id;
                                effectsText.append(Component.literal(" " + displayName).withStyle(ChatFormatting.AQUA));
                                if (i < list.size() - 1) effectsText.append(Component.literal(",").withStyle(ChatFormatting.GRAY));
                            }
                        }
                        lines.add(effectsText);
                    } else {
                        lines.add(Component.translatable("tooltip.divineworkshop.godly_blessing.no_effects").withStyle(ChatFormatting.RED));
                    }
                } else {
                    lines.add(Component.translatable("tooltip.divineworkshop.godly_blessing.no_effects").withStyle(ChatFormatting.RED));
                }

                if (!itemStack.is(Items.ENCHANTED_BOOK)) {
                    String keyName = ModBlessingClientEvents.BLESSING_TOGGLE_KEY.getTranslatedKeyMessage().getString();
                    lines.add(Component.translatable("tooltip.divineworkshop.godly_blessing.toggle", keyName));
                }
            }
            else if (enchantment == ModMythicEnchantments.KILLER_FIELD.get()) {
                double base = ModMythicConfig.KILLER_FIELD_BASE.get();
                double damagePercent = 6.25 * Math.pow(base, level);
                int radius = 2 + 2 * level;
                int interval = Math.max(1, 60 / level);
                boolean affectAll = ModMythicConfig.KILLER_FIELD_AFFECT_ALL.get();
                String targetType = Component.translatable(affectAll ? "tooltip.divineworkshop.killer_field.target_all" : "tooltip.divineworkshop.killer_field.target_hostile").getString();

                lines.add(Component.translatable("tooltip.divineworkshop.killer_field.line1", levelComp));
                lines.add(Component.translatable("tooltip.divineworkshop.killer_field.line2", radius, interval, String.format("%.2f", damagePercent)));
                lines.add(Component.translatable("tooltip.divineworkshop.killer_field.line3", targetType));

                if (isEquipped && entity != null) {
                    boolean isDisabled = itemStack.hasTag() && itemStack.getTag().getBoolean("KillerFieldDisabled");
                    String status = isDisabled ? statusOff : statusOn;
                    String keyName = ModBlessingClientEvents.BLESSING_TOGGLE_KEY.getTranslatedKeyMessage().getString();
                    lines.add(Component.translatable("tooltip.divineworkshop.killer_field.status", status, keyName));
                }
            }
            else if (enchantment == ModMythicEnchantments.HEAVENLY_SWORD_WILL.get()) {
                double base = ModMythicConfig.HEAVENLY_SWORD_WILL_BASE.get();
                double damagePercent = 400 * Math.pow(base, level);
                int radius = 10 + 20 * level;
                int cooldown = Math.max(1, 1000 / level);
                boolean affectAll = ModMythicConfig.HEAVENLY_SWORD_WILL_AFFECT_ALL.get();
                String targetType = Component.translatable(affectAll ? "tooltip.divineworkshop.killer_field.target_all" : "tooltip.divineworkshop.killer_field.target_hostile").getString();

                lines.add(Component.translatable("tooltip.divineworkshop.heavenly_sword_will.line1", levelComp));
                lines.add(Component.translatable("tooltip.divineworkshop.heavenly_sword_will.line2", radius, cooldown / 20f, String.format("%.2f", damagePercent)));
                lines.add(Component.translatable("tooltip.divineworkshop.heavenly_sword_will.line3", targetType));
                lines.add(Component.translatable("tooltip.divineworkshop.heavenly_sword_will.line4"));
            }
            else if (enchantment == ModMythicEnchantments.SOUL_REAVER.get()) {
                double a = ModMythicConfig.SOUL_REAVER_BASE_A.get();
                double b = ModMythicConfig.SOUL_REAVER_BASE_B.get();
                double baseCap = ModMythicConfig.SOUL_REAVER_BASE_CAP.get();
                double perKill = Math.pow(a, level - 1);
                double cap = baseCap * Math.pow(b, level - 1);

                lines.add(Component.translatable("tooltip.divineworkshop.soul_reaver.line1", levelComp));
                lines.add(Component.translatable("tooltip.divineworkshop.soul_reaver.line2", String.format("%.1f", perKill), String.format("%.1f", cap)));

                if (isEquipped && entity != null && entity instanceof Player player) {
                    CompoundTag clientData = player.getPersistentData();
                    double currentHealth = clientData.getDouble("SoulReaverHealthBonus");
                    double currentAttack = clientData.getDouble("SoulReaverAttackBonus");

                    if (currentHealth == 0 && currentAttack == 0) {
                        AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
                        AttributeInstance attackAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
                        if (healthAttr != null) {
                            var mod = healthAttr.getModifier(UndyingNirvanaHandler.SOUL_REAVER_HEALTH_UUID);
                            if (mod != null) currentHealth = mod.getAmount();
                        }
                        if (attackAttr != null) {
                            var mod = attackAttr.getModifier(UndyingNirvanaHandler.SOUL_REAVER_ATTACK_UUID);
                            if (mod != null) currentAttack = mod.getAmount();
                        }
                        if (currentHealth > 0 || currentAttack > 0) {
                            clientData.putDouble("SoulReaverHealthBonus", currentHealth);
                            clientData.putDouble("SoulReaverAttackBonus", currentAttack);
                        }
                    }

                    double healthPercent = cap > 0 ? Math.min(100, (currentHealth / cap) * 100) : 0;
                    double attackPercent = cap > 0 ? Math.min(100, (currentAttack / cap) * 100) : 0;
                    lines.add(Component.translatable("tooltip.divineworkshop.soul_reaver.status",
                            String.format("%.1f", currentHealth), String.format("%.1f", currentAttack),
                            String.format("%.1f", healthPercent), String.format("%.1f", attackPercent)));
                } else {
                    lines.add(Component.translatable("tooltip.divineworkshop.soul_reaver.status_unknown"));
                }
            }
            else if (enchantment == ModMythicEnchantments.SKY_WALKER.get()) {
                lines.add(Component.translatable("tooltip.divineworkshop.sky_walker.line1", levelComp));
            }

            nodes.add(new ModTooltipSorterDispatcher.DisplayNode(ModTooltipSorterDispatcher.WEIGHT_MYTHIC + level, lines));
        }
    }

    private static int getArmorTotalLevel(LivingEntity entity, Enchantment enchantment) {
        int total = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                total += EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack);
            }
        }
        return total;
    }
}