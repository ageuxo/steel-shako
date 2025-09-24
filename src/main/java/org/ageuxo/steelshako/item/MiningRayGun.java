package org.ageuxo.steelshako.item;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.*;
import org.ageuxo.steelshako.ModDamageTypes;
import org.ageuxo.steelshako.attachment.MiningRayCache;
import org.ageuxo.steelshako.attachment.ModAttachments;
import org.ageuxo.steelshako.charge.ChargeHolder;
import org.ageuxo.steelshako.item.component.ChargeComponent;
import org.ageuxo.steelshako.item.component.ModComponents;
import org.ageuxo.steelshako.render.geo.MiningRayGunRenderer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class MiningRayGun extends Item implements ChargeHolder, GeoItem {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static int RAMPUP_TIME = 30;
    public static int RAY_TICK_CHARGE_COST = 10;
    public static int RAY_RANGE = 10;

    private static final RawAnimation SPIN_UP_ANIM = RawAnimation.begin().thenPlay("spin_up");
    private static final RawAnimation SPINNING_ANIM = RawAnimation.begin().thenLoop("spinning");
    private static final RawAnimation SPIN_DOWN_ANIM = RawAnimation.begin().thenPlay("spin_down");

    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);

    public MiningRayGun(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        if (player.getUseItem().getItem() != this){
            player.startUsingItem(usedHand);
            if (level instanceof ServerLevel serverLevel) {
                triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(usedHand), serverLevel), "firing", "spin_up");
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingUseDuration) {
        tickBeam(level, livingEntity, stack);
    }

    protected void tickBeam(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack) {
        int rampup = stack.getOrDefault(ModComponents.RAY_RAMPUP, 0);
        if (rampup > RAMPUP_TIME) {
            ChargeComponent component = stack.getOrDefault(ModComponents.CHARGE.get(), new ChargeComponent(50000, 50000));
            if (component.charge() >= RAY_TICK_CHARGE_COST) {
                doBeam(level, livingEntity, stack);
                stack.set(ModComponents.CHARGE.get(), component.sub(RAY_TICK_CHARGE_COST)); // subtract charge, replace component
                if (level instanceof ServerLevel serverLevel) {
                    triggerAnim(livingEntity, GeoItem.getOrAssignId(stack, serverLevel), "firing", "spinning");
                }
            }
        } else {
            stack.set(ModComponents.RAY_RAMPUP, rampup + 1);
        }

    }

    protected void doBeam(@NotNull Level level, @NotNull LivingEntity shooter, @NotNull ItemStack stack) {
        Vec3 eyePos = shooter.getEyePosition();
        Vec3 lookAngle = shooter.getLookAngle();
        Vec3 rayEnd = lookAngle.scale(RAY_RANGE).add(eyePos);

        // Trace for blocks
        BlockHitResult hitResult = level.clip(new ClipContext(eyePos, rayEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter));

        if (hitResult.getType() != HitResult.Type.MISS) {
            rayEnd = hitResult.getLocation();
        }

        // Trace for Entities
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(shooter, eyePos, rayEnd, new AABB(0.6, 0.6, 0.6, 0.10, 0.10, 0.10), (Entity entity) -> canHitEntity(shooter, entity), RAY_RANGE);

        if (entityHitResult != null && entityHitResult.getType() == HitResult.Type.ENTITY) { // If hit entity, do damage
            RegistryAccess registryAccess = level.registryAccess();
            var damageSource = new DamageSource(registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ModDamageTypes.MINING_RAY),
                    null,
                    shooter
            );
            entityHitResult.getEntity().hurt(damageSource, 0.001f); // TODO replace with setting on fire?
        } else if (hitResult.getType() != HitResult.Type.MISS){ // If hit block, do mining
            BlockPos hitPos = hitResult.getBlockPos();
            LOGGER.debug("ray end: {}, hit pos: {}", rayEnd, hitPos);
            LevelChunk chunk = level.getChunkAt(hitPos);
            MiningRayCache rayCache = chunk.getData(ModAttachments.MINING_RAY_CACHE);
            rayCache.addProgress(level, shooter, stack, hitPos);
            LOGGER.debug("Adding progress to {}, heat: {}", hitPos, rayCache.blockHeat(hitPos));
            chunk.setData(ModAttachments.MINING_RAY_CACHE, rayCache);
        }
    }

    protected boolean canHitEntity(LivingEntity shooter, Entity target) {
        if (!target.canBeHitByProjectile()) {
            return false;
        } else {
            return shooter == null || !shooter.isPassengerOfSameVehicle(target);
        }
    }

    @Override
    public void onStopUsing(@NotNull ItemStack stack, @NotNull LivingEntity entity, int count) {
        stack.set(ModComponents.RAY_RAMPUP, 0);
        if (entity.level() instanceof ServerLevel serverLevel){
            triggerAnim(entity, GeoItem.getOrAssignId(stack, serverLevel), "firing", "spin_down");
        }
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity, int timeCharged) {
        stack.set(ModComponents.RAY_RAMPUP, 0);
        if (livingEntity.level() instanceof ServerLevel serverLevel){
            triggerAnim(livingEntity, GeoItem.getOrAssignId(stack, serverLevel), "firing", "spin_down");
        }
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return Integer.MAX_VALUE;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.CUSTOM;
    }

    public int getCharge() {
        ChargeComponent component = this.components().get(ModComponents.CHARGE.get());
        return component != null ? component.charge() : 0;
    }

    public int getMaxCharge() {
        ChargeComponent component = this.components().get(ModComponents.CHARGE.get());
        return component != null ? component.maxCharge() : 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "firing", state -> state.isCurrentAnimation(SPINNING_ANIM) ? PlayState.CONTINUE : PlayState.STOP)
                .triggerableAnim("spin_up", SPIN_UP_ANIM)
                .triggerableAnim("spinning", SPINNING_ANIM)
                .triggerableAnim("spin_down", SPIN_DOWN_ANIM)

        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.instanceCache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private MiningRayGunRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new MiningRayGunRenderer();
                }

                return this.renderer;
            }
        });
    }
}
