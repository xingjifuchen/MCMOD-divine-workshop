package com.divineworkshop.finalending.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;

import static com.divineworkshop.finalending.FinalEndingConstants.SCROLL_STEP;

public class ScrollablePanel {

    private final Screen parent;
    private final int contentTop;
    private final int contentBottom;
    private final int availableHeight;

    private int scrollOffset = 0;
    private int maxScroll = 0;
    private final List<AbstractWidget> contentWidgets = new ArrayList<>();
    private final List<Integer> originalYPositions = new ArrayList<>();

    private boolean isDragging = false;
    private final int scrollBarX;
    private final int scrollBarWidth = 6;

    public ScrollablePanel(Screen parent, int contentTop, int contentBottom) {
        this.parent = parent;
        this.contentTop = contentTop;
        this.contentBottom = contentBottom;
        this.availableHeight = contentBottom - contentTop;
        this.scrollBarX = (parent.width / 2) + 175;
    }

    public List<AbstractWidget> build(List<AbstractWidget> rawWidgets) {
        this.contentWidgets.clear();
        this.originalYPositions.clear();

        int maxBottom = this.contentTop;
        for (AbstractWidget w : rawWidgets) {
            int bottom = w.getY() + w.getHeight();
            if (bottom > maxBottom) maxBottom = bottom;
        }
        int totalContentHeight = maxBottom - this.contentTop + 20;
        this.maxScroll = Math.max(0, totalContentHeight - this.availableHeight);

        if (this.scrollOffset > this.maxScroll) this.scrollOffset = this.maxScroll;
        if (this.scrollOffset < 0) this.scrollOffset = 0;

        for (AbstractWidget w : rawWidgets) {
            this.originalYPositions.add(w.getY());
            this.contentWidgets.add(w);
        }

        updateWidgetPositions();
        return this.contentWidgets;
    }

    public void updateWidgetPositions() {
        for (int i = 0; i < this.contentWidgets.size(); i++) {
            AbstractWidget w = this.contentWidgets.get(i);
            int origY = this.originalYPositions.get(i);
            w.setY(origY - this.scrollOffset);

            boolean visible = w.getY() >= this.contentTop - 10 && (w.getY() + w.getHeight()) <= this.contentBottom + 10;
            w.active = visible;
            w.visible = visible;
        }
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int clipX = (parent.width / 2) - 190;
        int clipY = this.contentTop - 5;
        int clipWidth = 380;
        int clipHeight = this.availableHeight + 10;

        guiGraphics.enableScissor(clipX, clipY, clipX + clipWidth, clipY + clipHeight);
        for (AbstractWidget w : this.contentWidgets) {
            w.render(guiGraphics, mouseX, mouseY, partialTick);
        }
        guiGraphics.disableScissor();

        if (this.maxScroll > 0) {
            int barHeight = (int) ((double) this.availableHeight * this.availableHeight / (this.availableHeight + this.maxScroll));
            barHeight = Math.max(15, Math.min(barHeight, this.availableHeight));
            int barY = this.contentTop + (int) ((double) this.scrollOffset * (this.availableHeight - barHeight) / this.maxScroll);

            guiGraphics.fill(scrollBarX, this.contentTop, scrollBarX + scrollBarWidth, this.contentBottom, 0x33FFFFFF);
            int barColor = (this.isDragging || isMouseOverScrollBar(mouseX, mouseY)) ? 0xAAFFFFFF : 0x66FFFFFF;
            guiGraphics.fill(scrollBarX, barY, scrollBarX + scrollBarWidth, barY + barHeight, barColor);
        }
    }

    public boolean mouseScrolled(double delta) {
        if (this.maxScroll <= 0) return false;
        int newOffset = this.scrollOffset - (int)(delta * SCROLL_STEP);
        newOffset = Math.max(0, Math.min(newOffset, this.maxScroll));
        if (newOffset != this.scrollOffset) {
            this.scrollOffset = newOffset;
            updateWidgetPositions();
            return true;
        }
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.maxScroll > 0) {
            if (isMouseOverScrollBar(mouseX, mouseY)) {
                this.isDragging = true;
                updateScrollFromMouseY(mouseY);
                return true;
            }
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && this.isDragging) {
            updateScrollFromMouseY(mouseY);
            return true;
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.isDragging = false;
        }
        return false;
    }

    private boolean isMouseOverScrollBar(double mouseX, double mouseY) {
        return mouseX >= scrollBarX && mouseX <= scrollBarX + scrollBarWidth && mouseY >= this.contentTop && mouseY <= this.contentBottom;
    }

    private void updateScrollFromMouseY(double mouseY) {
        int barHeight = (int) ((double) this.availableHeight * this.availableHeight / (this.availableHeight + this.maxScroll));
        barHeight = Math.max(15, Math.min(barHeight, this.availableHeight));

        double relativeY = mouseY - this.contentTop - (barHeight / 2.0);
        double ratio = relativeY / (this.availableHeight - barHeight);

        int newOffset = (int) (ratio * this.maxScroll);
        this.scrollOffset = Math.max(0, Math.min(newOffset, this.maxScroll));
        updateWidgetPositions();
    }

    public int getScrollOffset() { return scrollOffset; }
    public void setScrollOffset(int offset) {
        this.scrollOffset = Math.max(0, Math.min(offset, this.maxScroll));
        updateWidgetPositions();
    }
    public int getMaxScroll() { return maxScroll; }
    public void resetScroll() { this.scrollOffset = 0; updateWidgetPositions(); }
}