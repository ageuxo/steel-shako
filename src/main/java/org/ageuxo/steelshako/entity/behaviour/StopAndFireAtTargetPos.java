package org.ageuxo.steelshako.entity.behaviour;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.DelayedBehaviour;
import net.tslat.smartbrainlib.object.FreePositionTracker;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.ageuxo.steelshako.entity.RangedPosAttacker;
import org.ageuxo.steelshako.item.RangedTargetWeapon;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class StopAndFireAtTargetPos<E extends LivingEntity & RangedPosAttacker> extends DelayedBehaviour<E> {

    public static final MemoryTest MEMORIES = MemoryTest.builder().hasMemories(MemoryModuleType.ATTACK_TARGET).noMemory(MemoryModuleType.ATTACK_COOLING_DOWN);

    protected Function<E, Float> attackRadiusSupplier = entity -> 16f;
    protected Function<E, Integer> attackIntervalSupplier = entity -> entity.level().getDifficulty() == Difficulty.HARD ? 20 : 40;

    protected @Nullable FreePositionTracker target;

    public StopAndFireAtTargetPos(int delayTicks) {
        super(delayTicks);
    }

    public StopAndFireAtTargetPos<E> attackRadius(Function<E, Float> attackRadiusSupplier) {
        this.attackRadiusSupplier = attackRadiusSupplier;

        return this;
    }

    public StopAndFireAtTargetPos<E> attackInterval(Function<E, Integer> attackIntervalSupplier) {
        this.attackIntervalSupplier = attackIntervalSupplier;

        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORIES;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        LivingEntity targetEntity = BrainUtils.getTargetOfEntity(entity);
        if (targetEntity != null &&
                BrainUtils.canSee(entity, targetEntity) && (entity.distanceToSqr(targetEntity) <= this.attackRadiusSupplier.apply(entity)) &&
                entity.getMainHandItem().getItem() instanceof RangedTargetWeapon
        ) {
            this.target = new FreePositionTracker(targetEntity.getEyePosition(0));
            return true;
        }

        return false;
    }

    @Override
    protected void start(E entity) {
        Brain<?> brain = entity.getBrain();
        brain.setMemory(MemoryModuleType.LOOK_TARGET, this.target);
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    @Override
    protected void stop(E entity) {
        this.target = null;
    }

    @Override
    protected void doDelayedAction(E entity) {
        if (this.target == null ||
                Mth.sqrt((float) entity.distanceToSqr(target.currentPosition())) > this.attackRadiusSupplier.apply(entity)) {
            return;
        }

        BrainUtils.setForgettableMemory(entity, MemoryModuleType.ATTACK_COOLING_DOWN, true, this.attackIntervalSupplier.apply(entity));
        entity.shoot();
    }
}
