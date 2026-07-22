package com.divineworkshop.finalending;

import com.divineworkshop.finalending.widget.ScrollablePanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.divineworkshop.finalending.FinalEndingConstants.ATTRIBUTE_NAMES;

@OnlyIn(Dist.CLIENT)
public class ModFinalEndingAttributeScreen extends Screen {

    private final ModFinalEndingScreen parent;
    private int scrollOffset = 0;
    private final List<AbstractWidget> fixedButtons = new ArrayList<>();
    private final Map<Attribute, EditBox> inputBoxes = new HashMap<>();
    private final Map<Attribute, AbstractSliderButton> sliders = new HashMap<>();
    private ScrollablePanel scrollablePanel;

    protected ModFinalEndingAttributeScreen(ModFinalEndingScreen parent) {
        super(Component.translatable("final_ending.attribute.screen.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();
        this.fixedButtons.clear();
        this.inputBoxes.clear();
        this.sliders.clear();

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

        Button toggleAttr = Button.builder(
                Component.translatable("final_ending.attribute.toggle", Component.translatable(ModFinalEndingConfig.d_attribute_modify ? "final_ending.state.on" : "final_ending.state.off")),
                b -> {
                    ModFinalEndingConfig.d_attribute_modify = !ModFinalEndingConfig.d_attribute_modify;
                    ModFinalEndingConfig.CURRENT_DEFENSE_PRESET = ModFinalEndingConfig.DefensePreset.CUSTOM;
                    b.setMessage(Component.translatable("final_ending.attribute.toggle", Component.translatable(ModFinalEndingConfig.d_attribute_modify ? "final_ending.state.on" : "final_ending.state.off")));
                }
        ).bounds(centerX - 150, centerY - 120, 300, 20).build();
        this.addRenderableWidget(toggleAttr);
        this.fixedButtons.add(toggleAttr);

        int contentTop = centerY - 90;
        int contentBottom = centerY + 120;

        List<AbstractWidget> rawContent = new ArrayList<>();
        int row = 0;

        for (Map.Entry<Attribute, String> entry : ATTRIBUTE_NAMES.entrySet()) {
            Attribute attr = entry.getKey();
            String nameKey = entry.getValue();
            String key = BuiltInRegistries.ATTRIBUTE.getKey(attr).toString();
            double currentValue = ModFinalEndingConfig.attr_values.getOrDefault(key, 0.0);

            int y = contentTop + row * 30;

            Button label = Button.builder(Component.translatable(nameKey), b -> {}).bounds(centerX - 170, y, 70, 20).build();
            label.active = false;
            rawContent.add(label);

            double maxVal = 1000000.0;
            AbstractSliderButton slider = new AbstractSliderButton(centerX - 95, y, 150, 20,
                    Component.literal(String.format("%.2f", currentValue)),
                    Math.min(currentValue / maxVal, 1.0)) {
                @Override
                protected void updateMessage() {
                    double val = this.value * maxVal;
                    this.setMessage(Component.literal(String.format("%.2f", val)));
                    EditBox box = inputBoxes.get(attr);
                    if (box != null) box.setValue(String.format("%.2f", val));
                }
                @Override
                protected void applyValue() {
                    double val = this.value * maxVal;
                    setAttributeConfigValue(attr, val);
                    ModFinalEndingConfig.CURRENT_DEFENSE_PRESET = ModFinalEndingConfig.DefensePreset.CUSTOM;
                }
            };
            rawContent.add(slider);
            sliders.put(attr, slider);

            EditBox input = new EditBox(this.font, centerX + 60, y, 80, 20, Component.translatable("final_ending.attribute.input_hint"));
            input.setValue(String.format("%.2f", currentValue));
            final Attribute finalAttr = attr;
            input.setResponder(s -> {
                try {
                    if (!s.isEmpty()) {
                        double val = Double.parseDouble(s);
                        if (val >= 0) {
                            if (val > Double.MAX_VALUE) val = Double.MAX_VALUE;
                            setAttributeConfigValue(finalAttr, val);
                            AbstractSliderButton sl = sliders.get(finalAttr);
                            if (sl != null) {
                                try {
                                    Field valueField = AbstractSliderButton.class.getDeclaredField("value");
                                    valueField.setAccessible(true);
                                    double max = 1000000.0;
                                    valueField.set(sl, Math.min(val / max, 1.0));
                                    Method updateMethod = AbstractSliderButton.class.getDeclaredMethod("updateMessage");
                                    updateMethod.setAccessible(true);
                                    updateMethod.invoke(sl);
                                } catch (Exception ignored) {}
                            }
                            ModFinalEndingConfig.CURRENT_DEFENSE_PRESET = ModFinalEndingConfig.DefensePreset.CUSTOM;
                        }
                    }
                } catch (NumberFormatException ignored) {}
            });
            rawContent.add(input);
            inputBoxes.put(attr, input);

            Button maxBtn = Button.builder(Component.translatable("final_ending.button.max"), b -> {
                double max = Double.MAX_VALUE;
                setAttributeConfigValue(finalAttr, max);
                EditBox box = inputBoxes.get(finalAttr);
                if (box != null) box.setValue(String.valueOf(max));
                AbstractSliderButton sl = sliders.get(finalAttr);
                if (sl != null) {
                    try {
                        Field valueField = AbstractSliderButton.class.getDeclaredField("value");
                        valueField.setAccessible(true);
                        valueField.set(sl, 1.0D);
                        Method updateMethod = AbstractSliderButton.class.getDeclaredMethod("updateMessage");
                        updateMethod.setAccessible(true);
                        updateMethod.invoke(sl);
                    } catch (Exception e) {
                        sl.setMessage(Component.literal(String.valueOf(max)));
                    }
                }
                ModFinalEndingConfig.CURRENT_DEFENSE_PRESET = ModFinalEndingConfig.DefensePreset.CUSTOM;
            }).bounds(centerX + 145, y, 40, 20).build();
            rawContent.add(maxBtn);

            row++;
        }

        this.scrollablePanel = new ScrollablePanel(this, contentTop, contentBottom);
        this.scrollablePanel.setScrollOffset(this.scrollOffset);
        List<AbstractWidget> positioned = this.scrollablePanel.build(rawContent);
        for (AbstractWidget w : positioned) {
            this.addRenderableWidget(w);
        }
    }

    private void setAttributeConfigValue(Attribute attr, double value) {
        String key = BuiltInRegistries.ATTRIBUTE.getKey(attr).toString();
        ModFinalEndingConfig.attr_values.put(key, value);
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
        guiGraphics.fillGradient(centerX - 180, centerY - 170, centerX + 180, centerY + 170, 0xAA1A0020, 0xAA0A0010);
        guiGraphics.drawCenteredString(this.font, Component.translatable("final_ending.attribute.screen.title").getString(), centerX, centerY - 160, 0xFFAA55);
        guiGraphics.drawString(this.font, Component.translatable("final_ending.attribute.hint").getString(), centerX - 170, centerY + 155, 0x888888);

        for (AbstractWidget w : this.fixedButtons) w.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.scrollablePanel != null) this.scrollablePanel.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}