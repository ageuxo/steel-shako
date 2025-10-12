package org.ageuxo.steelshako.item;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.network.PacketDistributor;
import org.ageuxo.steelshako.ModDamageTypes;
import org.ageuxo.steelshako.ModTags;
import org.ageuxo.steelshako.network.RayBeamPayload;
import org.ageuxo.steelshako.render.geo.RayPistolRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class RayPistol extends ProjectileWeaponItem implements RangedTargetWeapon, GeoItem {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);

    public RayPistol(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return Integer.MAX_VALUE;
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.is(ModTags.LASER_AMMO);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 16;
    }

    @Override
    public void shoot(LivingEntity shooter, ItemStack stack) {
        Level level = shooter.level();
        Vec3 eyePos = shooter.getEyePosition();
        Vec3 lookAngle = shooter.getViewVector(0);
        Vec3 rayEnd = lookAngle.scale(getDefaultProjectileRange()).add(eyePos);

        // Trace for blocks
        BlockHitResult hitResult = level.clip(new ClipContext(eyePos, rayEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter));

        if (hitResult.getType() != HitResult.Type.MISS) {
            rayEnd = hitResult.getLocation();
        }

        // Trace for Entities
        double min = 0.3;
        double max = 0.13;
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(shooter, eyePos, rayEnd, new AABB(min, min, min, max, max, max), (Entity entity) -> canHitEntity(shooter, entity), getDefaultProjectileRange());

        if (entityHitResult != null && entityHitResult.getType() == HitResult.Type.ENTITY) { // If hit entity, do damage
            rayEnd = entityHitResult.getLocation();
            RegistryAccess registryAccess = level.registryAccess();
            var damageSource = new DamageSource(registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ModDamageTypes.MINING_RAY),
                    null,
                    shooter
            );
            entityHitResult.getEntity().hurt(damageSource, 3f);
        } else if (hitResult.getType() != HitResult.Type.MISS) { // If hit block, do nothing
            rayEnd = hitResult.getLocation();
        }
        if (!level.isClientSide) {
            PacketDistributor.sendToPlayersTrackingEntity(shooter, new RayBeamPayload(shooter.getId(), rayEnd.toVector3f(), RayBeamPayload.Colour.ORANGE));
        }
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
        shoot(shooter, shooter.getMainHandItem());
    }

    protected boolean canHitEntity(LivingEntity shooter, Entity target) {
        if (!target.canBeHitByProjectile()) {
            return false;
        } else {
            return !shooter.isPassengerOfSameVehicle(target);
        }
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private RayPistolRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new RayPistolRenderer();
                }

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return instanceCache;
    }
}
