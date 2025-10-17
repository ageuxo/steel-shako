package org.ageuxo.steelshako.render.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.block.multi.MultiblockCoreBlockEntity;
import org.ageuxo.steelshako.menu.BoilerMenu;
import org.ageuxo.steelshako.render.screen.widget.FluidBarWidget;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BoilerScreen extends AbstractContainerScreen<BoilerMenu> {

    private static final ResourceLocation BACKPLATE_TEXTURE = SteelShakoMod.modRL("textures/gui/container/boiler/boiler.png");
    private static final ResourceLocation BURN_TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/sprites/container/furnace/lit_progress.png");

    private FluidBarWidget fluidBar;

    public BoilerScreen(BoilerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
        this.fluidBar.updateWidget(menu.water(), menu.maxWater());

        float fuelLevel = (float) menu.fuel() / menu.maxFuel();

        drawFuelLevel(guiGraphics, fuelLevel);
    }

    private void drawFuelLevel(GuiGraphics guiGraphics, float fuelLevel) {
        int burnSize = 28;
        int drawHeight = (int) (burnSize * fuelLevel);
        int x = leftPos + 74;
        int y = topPos + 17;
        int drawOffset = (burnSize - drawHeight);

        guiGraphics.blit(BURN_TEXTURE, x, y+drawOffset, 0f, drawOffset, 28, drawHeight, 28, 28);
    }

    @Override
    protected void init() {
        super.init();
        MultiblockCoreBlockEntity core = menu.blockEntity();
        this.fluidBar = new FluidBarWidget(leftPos+9, topPos+14, 22, 56, core::getFluidCapDirect, 0);
        this.addRenderableWidget(fluidBar);
    }

    //    vars width/height = width/height of game window
    //    vars imageWidth/imageHeight = width/height of Bg plate in image
    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = ((width-imageWidth)/2);
        int y = ((height-imageHeight)/2);

        guiGraphics.blit(BACKPLATE_TEXTURE, leftPos, topPos+2, 0, 0, imageWidth, imageHeight, 256, 256);

    }
}
