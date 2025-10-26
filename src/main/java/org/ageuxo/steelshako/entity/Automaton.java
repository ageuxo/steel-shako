package org.ageuxo.steelshako.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import org.ageuxo.steelshako.ModTags;
import org.ageuxo.steelshako.entity.behaviour.StopAndFireAtTargetPos;
import org.ageuxo.steelshako.item.ModItems;
import org.ageuxo.steelshako.item.RangedTargetWeapon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class Automaton extends Monster implements SmartBrainOwner<Automaton>, GeoEntity, RangedPosAttacker {

    protected static final RawAnimation HEAD_LOOK_ANIM = RawAnimation.begin().then("head_look", Animation.LoopType.PLAY_ONCE);
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation AIM_ANIM = RawAnimation.begin().thenPlay("ready").thenLoop("aim");
    protected static final RawAnimation VIBRATION_ANIM = RawAnimation.begin().thenLoop("vibrations");

    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenPlayAndHold("idle");
    protected static final RawAnimation FIRE_ANIM = RawAnimation.begin().thenPlayAndHold("fire");

    protected static final EntityDataAccessor<Integer> ATTACK_STATE = SynchedEntityData.defineId(Automaton.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);

    private int idleCooldown = 0;
    private int idleTimer = 0;
    private boolean shouldStartHeadLook = false;

    public Automaton(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        setDropChance(EquipmentSlot.MAINHAND, 0f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_STATE, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 25)
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.MOVEMENT_SPEED, 0.18D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 2.0D);
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.RAY_PISTOL));
    }

    @Override
    protected Brain.@NotNull Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem projectileWeapon) {
        return projectileWeapon == ModItems.RAY_PISTOL.get();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && ++idleTimer > idleCooldown) {
            idleCooldown = random.nextInt(100, 200);
            shouldStartHeadLook = true;
        }
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends Automaton>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>()
        );
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<Automaton> walk = new AnimationController<>(this, "walk", state -> {
            if (state.isMoving()) {
                return state.setAndContinue(WALK_ANIM);
            }
            return state.setAndContinue(IDLE_ANIM);
        });
        controllers.add(walk);

        AnimationController<Automaton> vibration = new AnimationController<>(this, "vibration", state -> state.setAndContinue(VIBRATION_ANIM));
        controllers.add(vibration);

        AnimationController<Automaton> weapon = new AnimationController<>(this, "weapon", state -> PlayState.CONTINUE)
                .triggerableAnim("aim", AIM_ANIM)
                .triggerableAnim("fire", FIRE_ANIM);
        controllers.add(weapon);

        AnimationController<Automaton> headLook = new AnimationController<>(this, "head_look", state -> {
            if (walk.getCurrentRawAnimation().equals(IDLE_ANIM) && !weapon.isPlayingTriggeredAnimation()) {
                if (shouldStartHeadLook) {
                    shouldStartHeadLook = false;
                    idleTimer = 0;
                    state.resetCurrentAnimation();
                    return state.setAndContinue(HEAD_LOOK_ANIM);
                } else {
                    return PlayState.CONTINUE;
                }

            }
            return PlayState.STOP;
        });
        controllers.add(headLook);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return instanceCache;
    }

    @Override
    public BrainActivityGroup<? extends Automaton> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends Automaton> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(
                        new TargetOrRetaliate<>()
                                .attackablePredicate(Automaton::isHostileTo),
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>().lookTime(e-> e.getRandom().nextInt(20, 80))
                ),
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<>().cooldownForBetween(40, 120),
                        new Idle<>().runFor(e -> e.getRandom().nextInt(30, 60))
                )
        );
    }

    @Override
    public BrainActivityGroup<? extends Automaton> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new SetWalkTargetToAttackTarget<>(),
                new StopAndFireAtTargetPos<>(60)
                        .whenActivating(this::fireAttack)
                        .whenStarting(this::chargeAttack)
                        .cooldownFor(e -> 80)
        );
    }

    public static boolean isHostileTo(LivingEntity target) {
        return target.getType().is(ModTags.AUTOMATON_MOB_TARGETS);
    }

    protected <E extends LivingEntity> void fireAttack(E e) {
        e.level().playSound(null, e.blockPosition(), SoundEvents.ARROW_HIT, SoundSource.HOSTILE);
        triggerAnim("weapon", "fire");
    }

    protected <E extends LivingEntity> void chargeAttack(E e) {
        e.level().playSound(null, e.blockPosition(), SoundEvents.CREEPER_PRIMED, SoundSource.HOSTILE);
        triggerAnim("weapon", "aim");
    }

    @Override
    public void shoot() {
        ItemStack handItem = getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof RangedTargetWeapon));
        if (handItem.getItem() instanceof RangedTargetWeapon weapon) {
            weapon.shoot(this, handItem);
        }
    }
}
