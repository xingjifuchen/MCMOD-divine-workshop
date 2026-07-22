package com.divineworkshop.finalending.builder;

import com.divineworkshop.finalending.ModFinalEndingConfig;
import com.divineworkshop.finalending.ModFinalEndingScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class PresetTabBuilder {

    private PresetTabBuilder() {}

    public static void build(int centerX, int baseY, int row, List<AbstractWidget> list, ModFinalEndingScreen screen) {
        int x0 = centerX - 170;

        Button attackLabel = Button.builder(Component.translatable("final_ending.preset.attack_label"), b -> {})
                .bounds(x0, baseY + row * 26, 80, 20).build();
        list.add(attackLabel);
        row++;

        ModFinalEndingConfig.AttackPreset[] attackPresets = {
                ModFinalEndingConfig.AttackPreset.OFF,
                ModFinalEndingConfig.AttackPreset.PRIMARY,
                ModFinalEndingConfig.AttackPreset.INTERMEDIATE,
                ModFinalEndingConfig.AttackPreset.ADVANCED,
                ModFinalEndingConfig.AttackPreset.ELITE,
                ModFinalEndingConfig.AttackPreset.ULTIMATE
        };
        int idxA = 0;
        for (ModFinalEndingConfig.AttackPreset ap : attackPresets) {
            int col = idxA % 3;
            int rowOff = idxA / 3;
            int xPos = x0 + col * 105;
            int yPos = baseY + (row + rowOff) * 26;
            boolean active = ModFinalEndingConfig.CURRENT_ATTACK_PRESET == ap;
            Button btn = Button.builder(
                    Component.literal((active ? "§a▶ " : "") + Component.translatable("final_ending.preset.attack." + ap.name().toLowerCase()).getString()),
                    b -> {
                        ModFinalEndingConfig.applyAttackPreset(ap);
                        screen.rebuildWidgets();
                    }
            ).bounds(xPos, yPos, 95, 20).build();
            list.add(btn);
            idxA++;
        }
        row += (attackPresets.length + 2) / 3 + 1;

        Button defLabel = Button.builder(Component.translatable("final_ending.preset.defense_label"), b -> {})
                .bounds(x0, baseY + row * 26, 80, 20).build();
        list.add(defLabel);
        row++;

        ModFinalEndingConfig.DefensePreset[] defensePresets = {
                ModFinalEndingConfig.DefensePreset.OFF,
                ModFinalEndingConfig.DefensePreset.LEVEL1,
                ModFinalEndingConfig.DefensePreset.LEVEL2,
                ModFinalEndingConfig.DefensePreset.LEVEL3,
                ModFinalEndingConfig.DefensePreset.LEVEL4,
                ModFinalEndingConfig.DefensePreset.LEVEL5
        };
        int idxD = 0;
        for (ModFinalEndingConfig.DefensePreset dp : defensePresets) {
            int col = idxD % 3;
            int rowOff = idxD / 3;
            int xPos = x0 + col * 105;
            int yPos = baseY + (row + rowOff) * 26;
            boolean active = ModFinalEndingConfig.CURRENT_DEFENSE_PRESET == dp;
            Button btn = Button.builder(
                    Component.literal((active ? "§a▶ " : "") + Component.translatable("final_ending.preset.defense." + dp.name().toLowerCase()).getString()),
                    b -> {
                        ModFinalEndingConfig.applyDefensePreset(dp);
                        screen.rebuildWidgets();
                    }
            ).bounds(xPos, yPos, 95, 20).build();
            list.add(btn);
            idxD++;
        }
        row += (defensePresets.length + 2) / 3 + 1;

        Button customBtn = Button.builder(
                Component.translatable("final_ending.preset.reset_custom"),
                b -> {
                    ModFinalEndingConfig.CURRENT_ATTACK_PRESET = ModFinalEndingConfig.AttackPreset.CUSTOM;
                    ModFinalEndingConfig.CURRENT_DEFENSE_PRESET = ModFinalEndingConfig.DefensePreset.CUSTOM;
                    screen.rebuildWidgets();
                }
        ).bounds(x0, baseY + row * 26, 200, 20).build();
        list.add(customBtn);
    }
}