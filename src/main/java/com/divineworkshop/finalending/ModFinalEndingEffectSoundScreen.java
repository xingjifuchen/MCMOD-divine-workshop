package com.divineworkshop.finalending;

import com.divineworkshop.finalending.widget.ScrollablePanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ModFinalEndingEffectSoundScreen extends Screen {

    private final ModFinalEndingScreen parent;
    private int scrollOffset = 0;
    private final List<AbstractWidget> fixedButtons = new ArrayList<>();
    private ScrollablePanel scrollablePanel;

    private static final String[][] FX_PAIRS = {
            {"final_ending.fx.lightning", "fx_lightning", "sfx_lightning"},
            {"final_ending.fx.void_tear", "fx_void_tear", "sfx_void_tear"},
            {"final_ending.fx.soul_explosion", "fx_soul_explosion", "sfx_soul_explosion"},
            {"final_ending.fx.celestial_cascade", "fx_celestial_cascade", "sfx_celestial_cascade"},
            {"final_ending.fx.phoenix_rebirth", "fx_phoenix_rebirth", "sfx_phoenix_rebirth"},
            {"final_ending.fx.chaos_rift", "fx_chaos_rift", "sfx_chaos_rift"},
            {"final_ending.fx.ender_rain", "fx_ender_rain", "sfx_ender_rain"},
            {"final_ending.fx.lunar_eclipse", "fx_lunar_eclipse", "sfx_lunar_eclipse"},
            {"final_ending.fx.blood_geyser", "fx_blood_geyser", "sfx_blood_geyser"},
            {"final_ending.fx.divine_judgement", "fx_divine_judgement", "sfx_divine_judgement"},
            {"final_ending.fx.shadow_veil", "fx_shadow_veil", "sfx_shadow_veil"},
            {"final_ending.fx.starfall", "fx_starfall", "sfx_starfall"}
    };

    protected ModFinalEndingEffectSoundScreen(ModFinalEndingScreen parent) {
        super(Component.translatable("final_ending.effectsound.screen.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();
        this.fixedButtons.clear();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        Button backBtn = Button.builder(Component.translatable("final_ending.button.back"), b -> {
            Minecraft.getInstance().setScreen(parent);
            parent.closeSubScreens();
        }).bounds(centerX - 150, centerY - 150, 120, 20).build();
        this.addRenderableWidget(backBtn);
        this.fixedButtons.add(backBtn);

        Button applyBtn = Button.builder(Component.translatable("final_ending.button.apply_and_back"), b -> {
            Minecraft.getInstance().setScreen(parent);
            parent.closeSubScreens();
            parent.rebuildWidgets();
        }).bounds(centerX - 50, centerY + 140, 100, 20).build();
        this.addRenderableWidget(applyBtn);
        this.fixedButtons.add(applyBtn);

        int contentTop = centerY - 120;
        int contentBottom = centerY + 120;

        List<AbstractWidget> rawContent = new ArrayList<>();
        int row = 0;

        for (String[] pair : FX_PAIRS) {
            String displayKey = pair[0];
            String fxField = pair[1];
            String sfxField = pair[2];

            boolean fxOn = getBoolField(fxField);
            boolean sfxOn = getBoolField(sfxField);

            int y = contentTop + row * 26;

            Button fxBtn = Button.builder(
                    Component.translatable(displayKey + ".fx", Component.translatable(fxOn ? "final_ending.state.on" : "final_ending.state.off")),
                    b -> {
                        toggleBoolField(fxField);
                        boolean newState = getBoolField(fxField);
                        b.setMessage(Component.translatable(displayKey + ".fx", Component.translatable(newState ? "final_ending.state.on" : "final_ending.state.off")));
                    }
            ).bounds(centerX - 170, y, 160, 20).build();
            rawContent.add(fxBtn);

            Button sfxBtn = Button.builder(
                    Component.translatable(displayKey + ".sfx", Component.translatable(sfxOn ? "final_ending.state.on" : "final_ending.state.off")),
                    b -> {
                        toggleBoolField(sfxField);
                        boolean newState = getBoolField(sfxField);
                        b.setMessage(Component.translatable(displayKey + ".sfx", Component.translatable(newState ? "final_ending.state.on" : "final_ending.state.off")));
                    }
            ).bounds(centerX, y, 100, 20).build();
            rawContent.add(sfxBtn);

            row++;
        }

        int yAll = contentTop + row * 26;
        row++;
        Button allOnBtn = Button.builder(Component.translatable("final_ending.effectsound.all_on"), b -> {
            for (String[] pair : FX_PAIRS) {
                setBoolField(pair[1], true);
                setBoolField(pair[2], true);
            }
            this.rebuildSubScreen();
        }).bounds(centerX - 170, yAll, 130, 20).build();
        rawContent.add(allOnBtn);

        Button allOffBtn = Button.builder(Component.translatable("final_ending.effectsound.all_off"), b -> {
            for (String[] pair : FX_PAIRS) {
                setBoolField(pair[1], false);
                setBoolField(pair[2], false);
            }
            this.rebuildSubScreen();
        }).bounds(centerX - 30, yAll, 130, 20).build();
        rawContent.add(allOffBtn);

        this.scrollablePanel = new ScrollablePanel(this, contentTop, contentBottom);
        this.scrollablePanel.setScrollOffset(this.scrollOffset);
        List<AbstractWidget> positioned = this.scrollablePanel.build(rawContent);
        for (AbstractWidget w : positioned) {
            this.addRenderableWidget(w);
        }
    }

    private boolean getBoolField(String name) {
        try {
            Field f = ModFinalEndingConfig.class.getField(name);
            return f.getBoolean(null);
        } catch (Exception e) {
            return false;
        }
    }

    private void toggleBoolField(String name) {
        try {
            Field f = ModFinalEndingConfig.class.getField(name);
            f.setBoolean(null, !f.getBoolean(null));
        } catch (Exception ignored) {}
    }

    private void setBoolField(String name, boolean value) {
        try {
            Field f = ModFinalEndingConfig.class.getField(name);
            f.setBoolean(null, value);
        } catch (Exception ignored) {}
    }

    private void rebuildSubScreen() {
        this.init(Minecraft.getInstance(), this.width, this.height);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.scrollablePanel != null && this.scrollablePanel.mouseScrolled(delta)) {
            this.scrollOffset = this.scrollablePanel.getScrollOffset();
            this.rebuildSubScreen();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0xCC000000, 0xCC000000);
        guiGraphics.fillGradient(centerX - 180, centerY - 170, centerX + 180, centerY + 170, 0xAA1A0020, 0xAA0A0010);
        guiGraphics.drawCenteredString(this.font, Component.translatable("final_ending.effectsound.screen.title").getString(), centerX, centerY - 160, 0xFFAA55);
        guiGraphics.drawString(this.font, Component.translatable("final_ending.effectsound.hint").getString(), centerX - 150, centerY + 150, 0x888888);

        for (AbstractWidget w : this.fixedButtons) w.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.scrollablePanel != null) this.scrollablePanel.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}