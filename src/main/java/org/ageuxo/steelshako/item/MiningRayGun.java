package org.ageuxo.steelshako.item;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.ageuxo.steelshako.ModDamageTypes;
import org.ageuxo.steelshako.ModSounds;
import org.ageuxo.steelshako.attachment.MiningRayCache;
import org.ageuxo.steelshako.attachment.ModAttachments;
import org.ageuxo.steelshako.item.component.ChargeComponent;
import org.ageuxo.steelshako.item.component.ModComponents;
import org.ageuxo.steelshako.network.RayBeamPayload;
import org.ageuxo.steelshako.render.geo.MiningRayGunRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

import java.util.WeakHashMap;
import java.util.function.Consumer;

public class MiningRayGun extends Item implements GeoItem {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final WeakHashMap<Entity, Long> RAY_SOUND_COOLDOWNS = new WeakHashMap<>();
    public static int RAMPUP_TIME = 30;
    public static int RAY_TICK_CHARGE_COST = 10;
    public static int RAY_RANGE = 40;

    private static final RawAnimation SPIN_UP_ANIM = RawAnimation.begin().thenPlay("spin_up");
    private static final RawAnimation SPINNING_ANIM = RawAnimation.begin().thenLoop("spinning");
    private static final RawAnimation SPIN_DOWN_ANIM = RawAnimation.begin().thenPlay("spin_down");

    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);

    public MiningRayGun(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        InteractionHand usedHand = context.getHand();
        Level level = context.getLevel();
        if (player != null && player.getUseItem().getItem() != this){
            startFiring(level, player, usedHand);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        if (player.getUseItem().getItem() != this){
            startFiring(level, player, usedHand);
        }
        return InteractionResultHolder.fail(player.getItemInHand(usedHand)); // Fail here works like consume does in other interaction methods
    }

    private void startFiring(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        player.startUsingItem(usedHand);
        level.playSound(player, player.blockPosition(), ModSounds.RAY_STARTUP.get(), SoundSource.PLAYERS, 1f, 1f);
        if (level instanceof ServerLevel serverLevel) {
            triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(usedHand), serverLevel), "firing", "spin_up");
        }
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingUseDuration) {
        tickBeam(level, livingEntity, stack);
    }

    protected void tickBeam(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack) {
        int rampup = stack.getOrDefault(ModComponents.RAY_RAMPUP, 0);
        if (rampup > RAMPUP_TIME) {
            ItemStack withCharge = getStackWithCharge(livingEntity);
            if (withCharge != null) {
                ChargeComponent component = withCharge.get(ModComponents.CHARGE.get());
                if (component != null && component.charge() >= RAY_TICK_CHARGE_COST && level.getGameTime() % 3 == 0) {
                    doBeam(level, livingEntity, stack);
                    withCharge.set(ModComponents.CHARGE.get(), component.sub(RAY_TICK_CHARGE_COST)); // subtract charge, replace component
                    if (level instanceof ServerLevel serverLevel) {
                        triggerAnim(livingEntity, GeoItem.getOrAssignId(stack, serverLevel), "firing", "spinning");
                    }
                }
            }
        } else {
            stack.set(ModComponents.RAY_RAMPUP, rampup + 1);
        }

    }

    protected @Nullable ItemStack getStackWithCharge(LivingEntity entity) {
        IItemHandler handler = entity.getCapability(Capabilities.ItemHandler.ENTITY);
        if (handler != null) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                ChargeComponent component = stack.get(ModComponents.CHARGE);
                if (component != null && component.charge() >= RAY_TICK_CHARGE_COST) {
                    return stack;
                }
            }
        }

        return null;
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
            rayEnd = entityHitResult.getLocation();
            RegistryAccess registryAccess = level.registryAccess();
            var damageSource = new DamageSource(registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ModDamageTypes.MINING_RAY),
                    null,
                    shooter
            );
            entityHitResult.getEntity().hurt(damageSource, 0.001f); // TODO replace with setting on fire?
        } else if (hitResult.getType() != HitResult.Type.MISS){ // If hit block, do mining
            rayEnd = hitResult.getLocation();
            BlockPos hitPos = hitResult.getBlockPos();
//            LOGGER.debug("ray end: {}, hit pos: {}", rayEnd, hitPos);
            LevelChunk chunk = level.getChunkAt(hitPos);
            addMiningProgress(level, shooter, stack, chunk, hitPos);
        }
        if (!level.isClientSide) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(shooter, new RayBeamPayload(shooter.getId(), rayEnd.toVector3f(), RayBeamPayload.Colour.RED));
            playSoundAtInterval(level, shooter);
        }
    }

    private static void addMiningProgress(@NotNull Level level, @NotNull LivingEntity shooter, @NotNull ItemStack stack, LevelChunk chunk, BlockPos hitPos) {
        MiningRayCache rayCache = chunk.getData(ModAttachments.MINING_RAY_CACHE);
        rayCache.addProgress(level, shooter, stack, hitPos);
//            LOGGER.debug("Adding progress to {}, heat: {}", hitPos, rayCache.blockHeat(hitPos));
        chunk.setData(ModAttachments.MINING_RAY_CACHE, rayCache);
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
        Level level = entity.level();
        if (level instanceof ServerLevel serverLevel){
            triggerAnim(entity, GeoItem.getOrAssignId(stack, serverLevel), "firing", "spin_down");
        }
        level.playSound(entity, entity.blockPosition(), ModSounds.RAY_FIRE_END.get(), SoundSource.PLAYERS, 1f, 1f);
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

    @Override
    public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
        return slotChanged || !ItemStack.isSameItem(oldStack, newStack) || !(oldStack.getCount() == newStack.getCount());
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

    private static final int RAY_SOUND_INTERVAL = 320;

    public static void playSoundAtInterval(Level level, LivingEntity livingEntity) {
        Long timestamp = RAY_SOUND_COOLDOWNS.getOrDefault(livingEntity, 0L);
        if (level.getGameTime() - timestamp >= RAY_SOUND_INTERVAL) {
            level.playSound(null, livingEntity.blockPosition(), ModSounds.RAY_FIRE_SUSTAIN.get(), SoundSource.PLAYERS, 1f, 1);
            RAY_SOUND_COOLDOWNS.put(livingEntity, level.getGameTime());
        }

    }

}
