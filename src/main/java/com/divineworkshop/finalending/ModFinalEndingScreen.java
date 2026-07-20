package com.divineworkshop.finalending;

import com.divineworkshop.finalending.builder.*;
import com.divineworkshop.finalending.widget.ScrollablePanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.divineworkshop.finalending.FinalEndingConstants.CONTENT_BOTTOM_OFFSET;
import static com.divineworkshop.finalending.FinalEndingConstants.CONTENT_TOP_OFFSET;

@OnlyIn(Dist.CLIENT)
public class ModFinalEndingScreen extends Screen {

    private int activeTab = 0;
    private int scrollOffset = 0;

    private final List<AbstractWidget> fixedButtons = new ArrayList<>();
    private ScrollablePanel scrollablePanel;

    @Nullable
    private ModFinalEndingDamageTypeScreen subScreen = null;
    @Nullable
    private ModFinalEndingAttributeScreen attributeSubScreen = null;
    @Nullable
    private ModFinalEndingEffectScreen effectSubScreen = null;
    @Nullable
    private ModFinalEndingEffectSoundScreen effectSoundSubScreen = null;

    public ModFinalEndingScreen() {
        super(Component.translatable("final_ending.main.screen.title"));
    }

    @Override
    protected void init() {
        super.init();
        this.subScreen = null;
        this.attributeSubScreen = null;
        this.effectSubScreen = null;
        this.effectSoundSubScreen = null;
        rebuildWidgets();
    }

    public void rebuildWidgets() {
        this.clearWidgets();
        this.fixedButtons.clear();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        String[] tabKeys = {"final_ending.tab.attack", "final_ending.tab.defense", "final_ending.tab.other", "final_ending.tab.preset"};
        int[] tabX = {centerX - 180, centerX - 95, centerX - 10, centerX + 75};
        for (int i = 0; i < tabKeys.length; i++) {
            final int index = i;
            Button btn = Button.builder(Component.translatable(tabKeys[i]), b -> {
                this.activeTab = index;
                this.scrollOffset = 0;
                this.rebuildWidgets();
            }).bounds(tabX[i], centerY - 140, 80, 20).build();
            if (this.activeTab == index) {
                btn.active = false;
            }
            this.addRenderableWidget(btn);
            this.fixedButtons.add(btn);
        }

        Button closeBtn = Button.builder(Component.translatable("final_ending.button.close"), b -> this.onClose())
                .bounds(centerX - 50, centerY + 100, 100, 20).build();
        this.addRenderableWidget(closeBtn);
        this.fixedButtons.add(closeBtn);

        int contentTop = centerY + CONTENT_TOP_OFFSET;
        int contentBottom = centerY + CONTENT_BOTTOM_OFFSET;

        List<AbstractWidget> rawContent = new ArrayList<>();
        int row = 0;

        switch (activeTab) {
            case 0 -> AttackTabBuilder.build(this, centerX, contentTop, row, rawContent);
            case 1 -> DefenseTabBuilder.build(centerX, contentTop, row, rawContent, this);
            case 2 -> OtherTabBuilder.build(centerX, contentTop, row, rawContent, this);
            case 3 -> PresetTabBuilder.build(centerX, contentTop, row, rawContent, this);
        }

        this.scrollablePanel = new ScrollablePanel(this, contentTop, contentBottom);
        this.scrollablePanel.setScrollOffset(this.scrollOffset);
        List<AbstractWidget> positioned = this.scrollablePanel.build(rawContent);
        for (AbstractWidget w : positioned) {
            this.addRenderableWidget(w);
        }

        this.setFocused(null);
    }

    public void openDamageTypeScreen() {
        if (this.subScreen == null) {
            this.subScreen = new ModFinalEndingDamageTypeScreen(this);
            Minecraft.getInstance().setScreen(this.subScreen);
        }
    }

    public void openAttributeScreen() {
        if (this.attributeSubScreen == null) {
            this.attributeSubScreen = new ModFinalEndingAttributeScreen(this);
            Minecraft.getInstance().setScreen(this.attributeSubScreen);
        }
    }

    public void openEffectScreen() {
        if (this.effectSubScreen == null) {
            this.effectSubScreen = new ModFinalEndingEffectScreen(this);
            Minecraft.getInstance().setScreen(this.effectSubScreen);
        }
    }

    public void openEffectSoundScreen() {
        if (this.effectSoundSubScreen == null) {
            this.effectSoundSubScreen = new ModFinalEndingEffectSoundScreen(this);
            Minecraft.getInstance().setScreen(this.effectSoundSubScreen);
        }
    }

    public void closeSubScreens() {
        this.subScreen = null;
        this.attributeSubScreen = null;
        this.effectSubScreen = null;
        this.effectSoundSubScreen = null;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.scrollablePanel != null && this.scrollablePanel.mouseScrolled(delta)) {
            this.scrollOffset = this.scrollablePanel.getScrollOffset();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.scrollablePanel != null && this.scrollablePanel.mouseClicked(mouseX, mouseY, button)) {
            this.scrollOffset = this.scrollablePanel.getScrollOffset();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.scrollablePanel != null && this.scrollablePanel.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            this.scrollOffset = this.scrollablePanel.getScrollOffset();
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.scrollablePanel != null) {
            this.scrollablePanel.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        guiGraphics.fillGradient(centerX - 200, centerY - 155, centerX + 200, centerY + 125, 0xEF15001A, 0xEF08000A);
        guiGraphics.fill(centerX - 201, centerY - 155, centerX - 200, centerY + 125, 0xFFE0A96D);
        guiGraphics.fill(centerX + 200, centerY - 155, centerX + 201, centerY + 125, 0xFFE0A96D);
        guiGraphics.fill(centerX - 201, centerY - 156, centerX + 201, centerY - 155, 0xFFE0A96D);
        guiGraphics.fill(centerX - 201, centerY + 125, centerX + 201, centerY + 126, 0xFFE0A96D);
        guiGraphics.drawCenteredString(this.font, Component.translatable("final_ending.main.title").getString(), centerX, centerY - 147, 0xFFE0A96D);
        guiGraphics.drawString(this.font, Component.translatable("final_ending.main.hint").getString(), centerX - 180, centerY + 103, 0xAAAAAA);

        if (this.subScreen != null || this.attributeSubScreen != null || this.effectSubScreen != null || this.effectSoundSubScreen != null) {
            super.render(guiGraphics, mouseX, mouseY, partialTick);
            return;
        }

        for (AbstractWidget w : this.fixedButtons) {
            w.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        if (this.scrollablePanel != null) {
            this.scrollablePanel.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }
}