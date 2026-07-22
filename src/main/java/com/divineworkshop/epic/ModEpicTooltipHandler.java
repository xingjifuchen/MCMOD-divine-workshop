package com.divineworkshop.epic;

import com.divineworkshop.client.ModTooltipSorterDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class ModEpicTooltipHandler {

    public static boolean hasEpic(Map<Enchantment, Integer> enchantments) {
        for (Enchantment enchantment : enchantments.keySet()) {
            if (enchantment == ModEpicEnchantments.UNIVERSE_RECEIVER.get() ||
                enchantment == ModEpicEnchantments.CURSE_EROSION.get() ||
                enchantment == ModEpicEnchantments.BLOOD_RETURN.get() ||
                enchantment == ModEpicEnchantments.SWIFT_STRIKES.get() ||
                enchantment == ModEpicEnchantments.LAST_STAND.get() ||
                enchantment == ModEpicEnchantments.LURKING_STRIKE.get()) {
                return true;
            }
        }
        return false;
    }

    public static void collectNodes(ItemTooltipEvent event, Map<Enchantment, Integer> enchantments, ItemStack itemStack, List<ModTooltipSorterDispatcher.DisplayNode> nodes) {
        LivingEntity viewer = event.getEntity();

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();

            Component levelComp = Component.literal("[LV ")
                    .append(Component.literal(String.valueOf(level)).withStyle(ChatFormatting.LIGHT_PURPLE))
                    .append(Component.literal("]").withStyle(ChatFormatting.LIGHT_PURPLE))
                    .withStyle(ChatFormatting.LIGHT_PURPLE);

            if (enchantment == ModEpicEnchantments.UNIVERSE_RECEIVER.get()) {
                List<Component> lines = new ArrayList<>();
                int baseMultiplier = (int) Math.pow(2, level);
                lines.add(Component.translatable("tooltip.divineworkshop.universe_receiver.line1", levelComp));
                lines.add(Component.translatable("tooltip.divineworkshop.universe_receiver.line2", baseMultiplier));
                nodes.add(new ModTooltipSorterDispatcher.DisplayNode(ModTooltipSorterDispatcher.WEIGHT_EPIC + level, lines));
            }
            else if (enchantment == ModEpicEnchantments.CURSE_EROSION.get()) {
                List<Component> lines = new ArrayList<>();
                int seconds = 5 + (level - 1) * 5;
                List<String> effectNames = new ArrayList<>();
                for (String effectId : ModEpicConfig.CURSE_EROSION_EFFECTS.get()) {
                    ResourceLocation loc = new ResourceLocation(effectId);
                    if (loc != null) {
                        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(loc);
                        if (effect != null) {
                            effectNames.add(Component.translatable(effect.getDescriptionId()).getString());
                        } else {
                            effectNames.add(effectId);
                        }
                    } else {
                        effectNames.add(effectId);
                    }
                }
                String effects = String.join(", ", effectNames);
                lines.add(Component.translatable("tooltip.divineworkshop.curse_erosion.line1", levelComp));
                lines.add(Component.translatable("tooltip.divineworkshop.curse_erosion.line2", level));
                lines.add(Component.translatable("tooltip.divineworkshop.curse_erosion.line3", seconds));
                lines.add(Component.translatable("tooltip.divineworkshop.curse_erosion.line4", effects));
                nodes.add(new ModTooltipSorterDispatcher.DisplayNode(ModTooltipSorterDispatcher.WEIGHT_EPIC + level, lines));
            }
            else if (enchantment == ModEpicEnchantments.BLOOD_RETURN.get()) {
                List<Component> lines = new ArrayList<>();
                double a = ModEpicConfig.BLOOD_RETURN_BASE_A.get();
                double b = ModEpicConfig.BLOOD_RETURN_BASE_B.get();
                double heal = 0.125 * Math.pow(a, level) * 100;
                double absorb = 3.125 * Math.pow(b, level);
                lines.add(Component.translatable("tooltip.divineworkshop.blood_return.line1", levelComp));
                lines.add(Component.translatable("tooltip.divineworkshop.blood_return.line2",
                        String.format("%.1f", heal), String.format("%.1f", absorb)));
                nodes.add(new ModTooltipSorterDispatcher.DisplayNode(ModTooltipSorterDispatcher.WEIGHT_EPIC + level, lines));
            }
            else if (enchantment == ModEpicEnchantments.SWIFT_STRIKES.get()) {
                List<Component> lines = new ArrayList<>();
                double speedBonus = 0.2 * level;
                lines.add(Component.translatable("tooltip.divineworkshop.swift_strikes.line1", levelComp));
                lines.add(Component.translatable("tooltip.divineworkshop.swift_strikes.line2",
                        String.format("%.2f", speedBonus)));
                nodes.add(new ModTooltipSorterDispatcher.DisplayNode(ModTooltipSorterDispatcher.WEIGHT_EPIC + level, lines));
            }
            else if (enchantment == ModEpicEnchantments.LAST_STAND.get()) {
                List<Component> lines = new ArrayList<>();
                double a = ModEpicConfig.LAST_STAND_BASE_A.get();
                double[] thresholds = {0.8, 0.6, 0.4, 0.2, 0.05};
                double[] baseMult = {0.1, 0.3, 0.5, 1.0, 3.0};
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < thresholds.length; i++) {
                    double mult = 1.0 + baseMult[i] * Math.pow(a, level - 1);
                    sb.append("≤").append((int)(thresholds[i]*100)).append("%: ").append(String.format("%.0f", mult*100)).append("%");
                    if (i < thresholds.length-1) sb.append(" | ");
                }
                lines.add(Component.translatable("tooltip.divineworkshop.last_stand.line1", levelComp));
                lines.add(Component.translatable("tooltip.divineworkshop.last_stand.line2", sb.toString()));
                nodes.add(new ModTooltipSorterDispatcher.DisplayNode(ModTooltipSorterDispatcher.WEIGHT_EPIC + level, lines));
            }
            else if (enchantment == ModEpicEnchantments.LURKING_STRIKE.get()) {
                List<Component> lines = new ArrayList<>();
                double a = ModEpicConfig.LURKING_STRIKE_BASE_A.get();
                double b = ModEpicConfig.LURKING_STRIKE_BASE_B.get();

                double perTickPercent = a * Math.pow(b, level) * 100;

                lines.add(Component.translatable("tooltip.divineworkshop.lurking_strike.line1", levelComp));
                lines.add(Component.translatable("tooltip.divineworkshop.lurking_strike.line2",
                        String.format("%.4f", perTickPercent)));

                if (viewer != null) {
                    int ticks = ModEpicEventHandler.getLurkingTicks(viewer.getUUID());
                    double seconds = ticks / 20.0;
                    double currentBonus = a * ticks * Math.pow(b, level) * 100;
                    lines.add(Component.translatable("tooltip.divineworkshop.lurking_strike.line3",
                            String.format("%.1f", seconds), String.format("%.2f", currentBonus)));
                }

                nodes.add(new ModTooltipSorterDispatcher.DisplayNode(ModTooltipSorterDispatcher.WEIGHT_EPIC + level, lines));
            }
        }
    }
}