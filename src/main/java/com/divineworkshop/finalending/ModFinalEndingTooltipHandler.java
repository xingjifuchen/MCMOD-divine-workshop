package com.divineworkshop.finalending;

import com.divineworkshop.client.ModTooltipSorterDispatcher;
import com.divineworkshop.utils.ModColorUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModFinalEndingTooltipHandler {

    public static boolean hasFinalEnding(Map<Enchantment, Integer> enchantments) {
        return enchantments.containsKey(ModFinalEndingEnchantments.FINAL_JUDGE.get()) 
            || enchantments.containsKey(ModFinalEndingEnchantments.FINAL_WALL.get());
    }

    public static void collectNodes(ItemTooltipEvent event, Map<Enchantment, Integer> enchantments, ItemStack itemStack, List<ModTooltipSorterDispatcher.DisplayNode> activeNodes) {
        int judgeLevel = enchantments.getOrDefault(ModFinalEndingEnchantments.FINAL_JUDGE.get(), 0);
        int wallLevel = enchantments.getOrDefault(ModFinalEndingEnchantments.FINAL_WALL.get(), 0);
        if (judgeLevel == 0 && wallLevel == 0) return;

        List<Component> finalEndingLines = new ArrayList<>();

        finalEndingLines.add(Component.empty());

        if (judgeLevel > 0) {
            finalEndingLines.add(ModColorUtils.makeRainbowComponent(Component.translatable("final_ending.tooltip.judge_line").getString(), 0.003F, 12));
        }
        if (wallLevel > 0) {
            finalEndingLines.add(ModColorUtils.makeRainbowComponent(Component.translatable("final_ending.tooltip.wall_line").getString(), 0.003F, 12));
        }

        String presetInfo = "";
        if (judgeLevel > 0 && wallLevel > 0) {
            String attackName = Component.translatable("final_ending.preset.attack." + ModFinalEndingConfig.CURRENT_ATTACK_PRESET.name().toLowerCase()).getString();
            String defenseName = Component.translatable("final_ending.preset.defense." + ModFinalEndingConfig.CURRENT_DEFENSE_PRESET.name().toLowerCase()).getString();
            presetInfo = Component.translatable("final_ending.tooltip.preset_both", attackName, defenseName).getString();
        } else if (judgeLevel > 0) {
            String name = Component.translatable("final_ending.preset.attack." + ModFinalEndingConfig.CURRENT_ATTACK_PRESET.name().toLowerCase()).getString();
            presetInfo = Component.translatable("final_ending.tooltip.preset_attack", name).getString();
        } else if (wallLevel > 0) {
            String name = Component.translatable("final_ending.preset.defense." + ModFinalEndingConfig.CURRENT_DEFENSE_PRESET.name().toLowerCase()).getString();
            presetInfo = Component.translatable("final_ending.tooltip.preset_defense", name).getString();
        }
        if (!presetInfo.isEmpty()) {
            finalEndingLines.add(ModColorUtils.makeRainbowComponent(presetInfo, 0.003F, 12));
        }

        String keyName = ModKeyBinding.OPEN_CONFIG_GUI.getTranslatedKeyMessage().getString();
        finalEndingLines.add(ModColorUtils.makeRainbowComponent(Component.translatable("final_ending.tooltip.key_hint", keyName).getString(), 0.003F, 12));

        activeNodes.add(new ModTooltipSorterDispatcher.DisplayNode(
            ModTooltipSorterDispatcher.WEIGHT_FINAL, 
            finalEndingLines
        ));
    }
}