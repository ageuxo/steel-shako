package org.ageuxo.steelshako.charge;

import net.minecraft.world.item.ItemStack;
import org.ageuxo.steelshako.item.component.ChargeComponent;
import org.ageuxo.steelshako.item.component.ModComponents;

public interface ChargeHolder {

    default int getCharge(ItemStack stack) {
        ChargeComponent component = stack.getComponents().get(ModComponents.CHARGE.get());
        return component != null ? component.charge() : 0;
    }

    default int getMaxCharge(ItemStack stack) {
        ChargeComponent component = stack.getComponents().get(ModComponents.CHARGE.get());
        return component != null ? component.maxCharge() : 0;
    }

    default int extractCharge(ItemStack stack, int amount) {
        ChargeComponent component = stack.getComponents().get(ModComponents.CHARGE.get());
        int got = 0;
        if (component != null) {
            got = Math.max(0, Math.min(amount, component.charge()));
            stack.set(ModComponents.CHARGE.get(), component.sub(got));
        }
        return got;
    }

    default int insertCharge(ItemStack stack, int amount) {
        ChargeComponent component = stack.getComponents().get(ModComponents.CHARGE.get());
        int got = 0;
        if (component != null) {
            got = Math.max(0, Math.min(amount, component.charge()));
            stack.set(ModComponents.CHARGE.get(), component.add(got));
        }
        return got;
    }
}
