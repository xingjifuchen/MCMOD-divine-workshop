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

import java.util.ArrayList;
import java.util.List;

import static com.divineworkshop.finalending.FinalEndingConstants.DAMAGE_TYPE_NAMES;

@OnlyIn(Dist.CLIENT)
public class ModFinalEndingDamageTypeScreen extends Screen {

    private final ModFinalEndingScreen parent;
    private int scrollOffset = 0;
    private final List<AbstractWidget> fixedButtons = new ArrayList<>();
    private ScrollablePanel scrollablePanel;

    protected ModFinalEndingDamageTypeScreen(ModFinalEndingScreen parent) {
        super(Component.translatable("final_ending.damagetype.screen.title"));
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
        int row = 0, col = 0;
        for (String type : DAMAGE_TYPE_NAMES.keySet()) {
            int x = centerX - 140 + col * 100;
            int y = contentTop + row * 26;
            
            String displayKey = DAMAGE_TYPE_NAMES.get(type);
            boolean initSelected = ModFinalEndingConfig.ACTIVE_DAMAGE_TYPES.contains(type);
            
            Button btn = Button.builder(
                    Component.translatable(displayKey + (initSelected ? ".selected" : ".unselected")),
                    b -> {
                        boolean currentlySelected = ModFinalEndingConfig.ACTIVE_DAMAGE_TYPES.contains(type);
                        if (currentlySelected) {
                            ModFinalEndingConfig.ACTIVE_DAMAGE_TYPES.remove(type);
                            b.setMessage(Component.translatable(displayKey + ".unselected"));
                        } else {
                            ModFinalEndingConfig.ACTIVE_DAMAGE_TYPES.add(type);
                            b.setMessage(Component.translatable(displayKey + ".selected"));
                        }
                        ModFinalEndingConfig.CURRENT_ATTACK_PRESET = ModFinalEndingConfig.AttackPreset.CUSTOM;
                    }
            ).bounds(x, y, 90, 20).build();
            
            rawContent.add(btn);
            col++;
            if (col >= 3) { col = 0; row++; }
        }

        this.scrollablePanel = new ScrollablePanel(this, contentTop, contentBottom);
        this.scrollablePanel.setScrollOffset(this.scrollOffset);
        List<AbstractWidget> positioned = this.scrollablePanel.build(rawContent);
        for (AbstractWidget w : positioned) {
            this.addRenderableWidget(w);
        }
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
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0xCC000000, 0xCC000000);
        guiGraphics.fillGradient(centerX - 170, centerY - 170, centerX + 170, centerY + 170, 0xAA1A0020, 0xAA0A0010);
        guiGraphics.drawCenteredString(this.font, Component.translatable("final_ending.damagetype.screen.title").getString(), centerX, centerY - 160, 0xFFAA55);
        guiGraphics.drawString(this.font, Component.translatable("final_ending.damagetype.hint").getString(), centerX - 150, centerY + 150, 0x888888);

        for (AbstractWidget w : this.fixedButtons) w.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.scrollablePanel != null) this.scrollablePanel.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}