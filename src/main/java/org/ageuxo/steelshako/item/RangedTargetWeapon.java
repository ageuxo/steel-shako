package org.ageuxo.steelshako.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface RangedTargetWeapon {
    void shoot(LivingEntity shooter, ItemStack stack);
}
