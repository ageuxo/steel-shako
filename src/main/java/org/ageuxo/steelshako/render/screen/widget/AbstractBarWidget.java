package org.ageuxo.steelshako.render.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBarWidget extends AbstractWidget {
    protected static final int OUTLINE_THICKNESS = 1;
    protected static final int DEFAULT_OUTLINE_COLOUR = 0xff8e8e8e;
    protected final int outlineColour;
    protected int barOffset;
    protected String barLocale;
    private int prevValue;
    private int prevMaxValue;
    private boolean requiresTooltipUpdate = false;
    private float fillAmount;

    public AbstractBarWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, int outlineColour) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.outlineColour = outlineColour;
    }

    public AbstractBarWidget(int pX, int pY, int pWidth, int pHeight) {
        this(pX, pY, pWidth, pHeight, Component.empty(), DEFAULT_OUTLINE_COLOUR);
    }

    public abstract void renderBar(@NotNull GuiGraphics guiGraphics, float fillAmount);

    public void updateWidget(int currentValue, int maxValue) {
        int scaledValue = currentValue * this.getHeight() / Math.max(maxValue, 1);
        this.barOffset = this.height - scaledValue;
        this.fillAmount = (float) currentValue / maxValue;
        if (this.requiresTooltipUpdate || currentValue != this.prevValue || maxValue != prevMaxValue) {
            this.prevValue = currentValue;
            this.prevMaxValue = maxValue;
            this.setTooltip(Tooltip.create(this.createTooltipComponent()));
        }
    }

    protected Component createTooltipComponent(){
        return Component.translatable(this.barLocale, prevValue, prevMaxValue);
    }

    protected void updateTooltip(){
        this.requiresTooltipUpdate = true;
    }

    protected boolean shouldUpdateTooltip(){
        return this.requiresTooltipUpdate;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
//        guiGraphics.renderOutline( this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.outlineColour);
        guiGraphics.fill( this.getX()+ AbstractBarWidget.OUTLINE_THICKNESS, this.getY()+ AbstractBarWidget.OUTLINE_THICKNESS, this.getX()+this.getWidth()- AbstractBarWidget.OUTLINE_THICKNESS, this.getY()+getHeight()- AbstractBarWidget.OUTLINE_THICKNESS, 0x00202020);
        this.renderBar(guiGraphics, this.fillAmount);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {

    }
}