package com.divineworkshop.finalending.builder;

import com.divineworkshop.finalending.ModFinalEndingConfig;
import com.divineworkshop.finalending.ModFinalEndingScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class DefenseTabBuilder {

    private DefenseTabBuilder() {}

    public static void build(int centerX, int baseY, int row, List<AbstractWidget> list, ModFinalEndingScreen screen) {
        int x0 = centerX - 170;

        String[][] defenseToggles = {
                {"final_ending.defense.toggle.cancel_attack", "d_cancel_attack"},
                {"final_ending.defense.toggle.cancel_hurt", "d_cancel_hurt"},
                {"final_ending.defense.toggle.cancel_damage", "d_cancel_damage"},
                {"final_ending.defense.toggle.cancel_death", "d_cancel_death"},
                {"final_ending.defense.toggle.force_heal", "d_force_heal"},
                {"final_ending.defense.toggle.cancel_knockback", "d_cancel_knockback"},
                {"final_ending.defense.toggle.cancel_dimension", "d_cancel_dimension"},
                {"final_ending.defense.toggle.tick_force_heal", "d_tick_force_heal"},
                {"final_ending.defense.toggle.invulnerable", "d_invulnerable"},
                {"final_ending.defense.toggle.revive_on_death", "d_revive_on_death"},
                {"final_ending.defense.toggle.creative_flight", "d_creative_flight"},
                {"final_ending.defense.toggle.remove_all_effects", "d_remove_all_effects"},
                {"final_ending.defense.toggle.remove_negative_effects", "d_remove_negative_effects"},
                {"final_ending.defense.toggle.prevent_disarm", "d_prevent_disarm"},
                {"final_ending.defense.toggle.reflect_damage", "d_reflect_damage"},
                {"final_ending.defense.toggle.lock_max_health", "d_lock_max_health"}
        };

        int idx = 0;
        for (String[] pair : defenseToggles) {
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

        int usedRows = (defenseToggles.length + 1) / 2;
        row += usedRows + 1;

        int yAttrEntry = baseY + row * 26;
        row++;
        Button attrEntryBtn = Button.builder(
                Component.translatable("final_ending.defense.attribute_entry", Component.translatable(ModFinalEndingConfig.d_attribute_modify ? "final_ending.state.on" : "final_ending.state.off")),
                b -> screen.openAttributeScreen()
        ).bounds(x0, yAttrEntry, 320, 20).build();
        list.add(attrEntryBtn);
    }

    private static boolean getBoolField(String name) {
        try { return ModFinalEndingConfig.class.getField(name).getBoolean(null); }
        catch (Exception e) { return false; }
    }

    private static void toggleBoolField(String name) {
        try {
            java.lang.reflect.Field f = ModFinalEndingConfig.class.getField(name);
            f.setBoolean(null, !f.getBoolean(null));
            ModFinalEndingConfig.CURRENT_DEFENSE_PRESET = ModFinalEndingConfig.DefensePreset.CUSTOM;
        } catch (Exception ignored) {}
    }
}