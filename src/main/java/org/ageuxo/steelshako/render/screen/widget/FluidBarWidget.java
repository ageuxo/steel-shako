package org.ageuxo.steelshako.render.screen.widget;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.ageuxo.steelshako.render.screen.ScreenUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class FluidBarWidget extends AbstractBarWidget {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String LOCALE_KEY = "gui.steel_shako.widget.bar.fluid";
    private final Supplier<IFluidHandler> fluidHandler;
    private FluidStack fluidStack;
    private final int tankIndex;

    public FluidBarWidget(int pX, int pY, Supplier<IFluidHandler> lazyFluidHandler, int tankIndex) {
        this(pX, pY, 25, 100, lazyFluidHandler, tankIndex);
    }

    public FluidBarWidget(int pX, int pY, int pWidth, int pHeight, Supplier<IFluidHandler> lazyFluidHandler, int tankIndex) {
        this(pX, pY, pWidth, pHeight, Component.empty(), DEFAULT_OUTLINE_COLOUR, lazyFluidHandler, tankIndex);
    }

    public FluidBarWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, int outlineColour, Supplier<IFluidHandler> fluidHandler, int tankIndex) {
        super(pX, pY, pWidth, pHeight, pMessage, outlineColour);
        this.fluidHandler = fluidHandler;
        this.tankIndex = tankIndex;
        this.barLocale = LOCALE_KEY;
        this.fluidStack = FluidStack.EMPTY;
    }

    @Override
    public void updateWidget(int currentValue, int maxValue) {
        IFluidHandler tank = fluidHandler.get();
        FluidStack tankFluid = tank.getFluidInTank(tankIndex);
        if (this.fluidStack != tankFluid){
            this.fluidStack = tankFluid;
            updateTooltip();
        }
        super.updateWidget(currentValue, maxValue);
    }

    @Override
    protected Component createTooltipComponent() {
        return Component.empty()
                .append(this.fluidStack.getHoverName().plainCopy().withStyle(ChatFormatting.BOLD))
                .append("\n")
                .append(super.createTooltipComponent());
    }

    @Override
    public void renderBar(@NotNull GuiGraphics guiGraphics, float fillAmount) {
        ScreenUtils.renderFluidBar(guiGraphics, this.getX()+1, this.getY()+this.height-1, this.width-2, this.height-2, this.fluidStack, fillAmount);
    }
}