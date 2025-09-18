package org.ageuxo.steelshako.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.ageuxo.steelshako.ModTags;
import org.ageuxo.steelshako.entity.projectile.Ray;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class RayGun extends BowItem {
    public RayGun(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return (i) -> i.is(ModTags.LASER_AMMO);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 24;
    }

    @Override
    protected void shootProjectile(@NotNull LivingEntity shooter, @NotNull Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
        projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot() + angle, 0.0F, velocity, 0);
    }

    @Override
    protected @NotNull Projectile createProjectile(@NotNull Level level, @NotNull LivingEntity shooter, @NotNull ItemStack weapon, @NotNull ItemStack ammo, boolean isCrit) {
        Ray ray = new Ray(level);
        ray.setPos(shooter.getEyePosition());
        ray.setDamage(4); // TODO set damage from stacks
        return ray;
    }

}
