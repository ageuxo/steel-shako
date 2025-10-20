package org.ageuxo.steelshako.render;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;
import org.ageuxo.steelshako.item.component.ChargeComponent;
import org.ageuxo.steelshako.item.component.ModComponents;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ChargeItemDecorator implements IItemDecorator {

    public static final ChargeItemDecorator INSTANCE = new ChargeItemDecorator();

    private ChargeItemDecorator() { }

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        ChargeComponent component = stack.get(ModComponents.CHARGE);
        if (component != null) {
            int maxWidth = 14;
            int width = Math.round((float) component.charge() * maxWidth / component.maxCharge());
            int x = xOffset + 1;
            int y = yOffset + 13;
            guiGraphics.fill(RenderType.guiOverlay(), x, y, x + maxWidth, y + 2, 0xFF000000);
            guiGraphics.fillGradient(RenderType.guiOverlay(), x, y, x + width, y + 1, 0xFFFF0000, 0xFFFF00FF, 200);

        }
        return false;
    }


}
