package com.divineworkshop.finalending.builder;

import com.divineworkshop.finalending.ModFinalEndingConfig;
import com.divineworkshop.finalending.ModFinalEndingScreen;
import com.divineworkshop.finalending.network.C2SRangeAttackPacket;
import com.divineworkshop.finalending.network.ModFinalEndingNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.List;

public final class AttackTabBuilder {

    private AttackTabBuilder() {}

    public static void build(ModFinalEndingScreen screen, int centerX, int baseY, int row, List<AbstractWidget> list) {
        int x0 = centerX - 170;
        int y0 = baseY + row++ * 26;

        AbstractSliderButton damageSlider = new AbstractSliderButton(x0, y0, 150, 20,
                Component.translatable("final_ending.attack.damage", (int) ModFinalEndingConfig.DAMAGE_AMOUNT),
                Math.min(ModFinalEndingConfig.DAMAGE_AMOUNT / 1000000.0F, 1.0F)) {
            @Override protected void updateMessage() {
                this.setMessage(Component.translatable("final_ending.attack.damage", (int) ModFinalEndingConfig.DAMAGE_AMOUNT));
            }
            @Override protected void applyValue() {
                ModFinalEndingConfig.DAMAGE_AMOUNT = (float)(this.value * 1000000.0D);
                ModFinalEndingConfig.CURRENT_ATTACK_PRESET = ModFinalEndingConfig.AttackPreset.CUSTOM;
            }
        };
        list.add(damageSlider);

        EditBox damageInput = new EditBox(Minecraft.getInstance().font, x0 + 155, y0, 60, 20, Component.translatable("final_ending.attack.damage_input"));
        damageInput.setValue(String.valueOf((int) ModFinalEndingConfig.DAMAGE_AMOUNT));
        damageInput.setResponder(s -> {
            try { if (!s.isEmpty()) { float val = Float.parseFloat(s); if (val >= 0) { ModFinalEndingConfig.DAMAGE_AMOUNT = val; ModFinalEndingConfig.CURRENT_ATTACK_PRESET = ModFinalEndingConfig.AttackPreset.CUSTOM; } } }
            catch (NumberFormatException ignored) {}
        });
        list.add(damageInput);

        Button maxBtn = Button.builder(Component.translatable("final_ending.button.max"), b -> {
            ModFinalEndingConfig.DAMAGE_AMOUNT = Float.MAX_VALUE;
            ModFinalEndingConfig.CURRENT_ATTACK_PRESET = ModFinalEndingConfig.AttackPreset.CUSTOM;
        }).bounds(x0 + 220, y0, 40, 20).build();
        list.add(maxBtn);

        row++;
        int y1 = baseY + row++ * 26;
        Button typeBtn = Button.builder(
                Component.translatable("final_ending.attack.damage_type", ModFinalEndingConfig.ACTIVE_DAMAGE_TYPES.size()),
                b -> screen.openDamageTypeScreen()
        ).bounds(x0, y1, 320, 20).build();
        list.add(typeBtn);

        row++;
        int y2 = baseY + row++ * 26;
        boolean isGlobal = ModFinalEndingConfig.ATTACK_RANGE < 0;
        float displayRange = isGlobal ? 100.0F : ModFinalEndingConfig.ATTACK_RANGE;
        double sliderValue = isGlobal ? 1.0 : Math.min(displayRange / 100.0, 1.0);
        AbstractSliderButton rangeSlider = new AbstractSliderButton(x0, y2, 250, 20,
                isGlobal ? Component.translatable("final_ending.attack.range_global") : Component.translatable("final_ending.attack.range", (int) displayRange),
                sliderValue) {
            @Override protected void updateMessage() {
                if (this.value >= 0.99) {
                    this.setMessage(Component.translatable("final_ending.attack.range_global"));
                } else {
                    int range = (int)(this.value * 100);
                    this.setMessage(Component.translatable("final_ending.attack.range", range));
                }
            }
            @Override protected void applyValue() {
                if (this.value >= 0.99) {
                    ModFinalEndingConfig.ATTACK_RANGE = -1.0F;
                } else {
                    ModFinalEndingConfig.ATTACK_RANGE = (float)(this.value * 100);
                }
                ModFinalEndingConfig.CURRENT_ATTACK_PRESET = ModFinalEndingConfig.AttackPreset.CUSTOM;
            }
        };
        list.add(rangeSlider);

        row++;
        int yFeedback = baseY + row++ * 26;
        boolean feedbackOn = ModFinalEndingConfig.d_attack_feedback;
        Button feedbackBtn = Button.builder(
                Component.translatable("final_ending.attack.feedback", Component.translatable(feedbackOn ? "final_ending.state.on" : "final_ending.state.off")),
                b -> {
                    ModFinalEndingConfig.d_attack_feedback = !ModFinalEndingConfig.d_attack_feedback;
                    ModFinalEndingConfig.CURRENT_ATTACK_PRESET = ModFinalEndingConfig.AttackPreset.CUSTOM;
                    boolean newState = ModFinalEndingConfig.d_attack_feedback;
                    b.setMessage(Component.translatable("final_ending.attack.feedback", Component.translatable(newState ? "final_ending.state.on" : "final_ending.state.off")));
                }
        ).bounds(x0, yFeedback, 320, 20).build();
        list.add(feedbackBtn);

        String[][] attackToggles = {
                {"final_ending.attack.toggle.set_health_zero", "m_set_health_zero"},
                {"final_ending.attack.toggle.call_die", "m_call_die"},
                {"final_ending.attack.toggle.set_max_health_zero", "m_set_max_health_zero"},
                {"final_ending.attack.toggle.set_max_health_zero_v2", "m_set_max_health_zero_v2"},
                {"final_ending.attack.toggle.set_removed", "m_set_removed"},
                {"final_ending.attack.toggle.discard", "m_discard"},
                {"final_ending.attack.toggle.disarm", "m_disarm"},
                {"final_ending.attack.toggle.prevent_spawning", "d_prevent_spawning"}
        };
        int idx = 0;
        for (String[] pair : attackToggles) {
            int col = idx % 2;
            int rowOff = idx / 2;
            int xPos = x0 + col * 165;
            int yPos = baseY + (row + rowOff) * 26;
            String fieldName = pair[1];
            boolean current = getBoolField(fieldName);
            Button btn = Button.builder(
                    Component.translatable(pair[0], Component.translatable(current ? "final_ending.state.on" : "final_ending.state.off")),
                    b -> {
                        toggleBoolField(fieldName);
                        boolean newState = getBoolField(fieldName);
                        b.setMessage(Component.translatable(pair[0], Component.translatable(newState ? "final_ending.state.on" : "final_ending.state.off")));
                    }
            ).bounds(xPos, yPos, 155, 20).build();
            list.add(btn);
            idx++;
        }

        int usedRows = (attackToggles.length + 1) / 2;
        row += usedRows + 1;

        row++;
        int ySep = baseY + row * 26;
        row++;
        Button sep = Button.builder(Component.translatable("final_ending.attack.separator"), b -> {})
                .bounds(x0, ySep, 320, 20).build();
        sep.active = false;
        list.add(sep);

        int yExec = baseY + row * 26;
        row++;
        Button execBtn = Button.builder(
                Component.translatable("final_ending.attack.execute"),
                b -> {
                    ModFinalEndingNetwork.INSTANCE.sendToServer(new C2SRangeAttackPacket());
                }
        ).bounds(x0, yExec, 200, 20).build();
        list.add(execBtn);

        boolean autoOn = ModFinalEndingConfig.d_auto_attack;
        Button autoBtn = Button.builder(
                Component.translatable("final_ending.attack.auto", Component.translatable(autoOn ? "final_ending.state.on" : "final_ending.state.off")),
                b -> {
                    ModFinalEndingConfig.d_auto_attack = !ModFinalEndingConfig.d_auto_attack;
                    boolean newState = ModFinalEndingConfig.d_auto_attack;
                    b.setMessage(Component.translatable("final_ending.attack.auto", Component.translatable(newState ? "final_ending.state.on" : "final_ending.state.off")));
                }
        ).bounds(x0 + 205, yExec, 80, 20).build();
        list.add(autoBtn);

        String[][] lootToggles = {
                {"final_ending.attack.loot.clear_all_entities", "clear_all_entities"},
                {"final_ending.attack.loot.force_drop", "loot_force_drop"},
                {"final_ending.attack.loot.erase_drops", "loot_erase_drops"},
                {"final_ending.attack.loot.erase_exp", "loot_erase_exp"}
        };
        int idxL = 0;
        for (String[] pair : lootToggles) {
            int col = idxL % 2;
            int rowOff = idxL / 2;
            int xPos = x0 + col * 165;
            int yPos = baseY + (row + rowOff) * 26;
            String fieldName = pair[1];
            boolean current = getBoolField(fieldName);
            Button btn = Button.builder(
                    Component.translatable(pair[0], Component.translatable(current ? "final_ending.state.on" : "final_ending.state.off")),
                    b -> {
                        toggleBoolField(fieldName);
                        boolean newState = getBoolField(fieldName);
                        b.setMessage(Component.translatable(pair[0], Component.translatable(newState ? "final_ending.state.on" : "final_ending.state.off")));
                    }
            ).bounds(xPos, yPos, 155, 20).build();
            list.add(btn);
            idxL++;
        }
    }

    private static boolean getBoolField(String name) {
        try {
            Field f = ModFinalEndingConfig.class.getField(name);
            return f.getBoolean(null);
        } catch (Exception e) {
            return false;
        }
    }

    private static void toggleBoolField(String name) {
        try {
            Field f = ModFinalEndingConfig.class.getField(name);
            f.setBoolean(null, !f.getBoolean(null));
            ModFinalEndingConfig.CURRENT_ATTACK_PRESET = ModFinalEndingConfig.AttackPreset.CUSTOM;
        } catch (Exception ignored) {}
    }
}