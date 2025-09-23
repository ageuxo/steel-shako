package org.ageuxo.steelshako.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Ray extends Projectile {
    public static EntityDataAccessor<Integer> RAY_DMG = SynchedEntityData.defineId(Ray.class, EntityDataSerializers.INT);

    public Ray(EntityType<Ray> entityType, Level level) {
        super(entityType, level);
        updateRotation();
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 pos = this.position();
        Vec3 deltaMove = this.getDeltaMovement();

        Vec3 futurePos = pos.add(deltaMove.scale(0.02));
        // clip to block
        HitResult hitResult = this.level().clip(new ClipContext(pos, futurePos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hitResult.getType() != HitResult.Type.MISS) {
            futurePos = hitResult.getLocation();
        }

        // find entity between currentPos and futurePos
        EntityHitResult entityHitResult = findHitEntity(pos, futurePos);
        if (entityHitResult != null && entityHitResult.getType() != HitResult.Type.MISS) {
            hitResult = entityHitResult;
        }

        if (!EventHooks.onProjectileImpact(this, hitResult)) {
            hitTargetOrDeflectSelf(hitResult);
            this.hasImpulse = true; // AbstractArrow does this, so we'd better
        }

        // set futurePos to collision point
        if (hitResult.getType() != HitResult.Type.MISS) {
            futurePos = hitResult.getLocation();
        }

        if (!this.noPhysics){
            // set position to new position
            this.setPos(futurePos);
            updateRotation();
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        this.remove(RemovalReason.DISCARDED);

    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 from, Vec3 to) {
        return ProjectileUtil.getEntityHitResult(
                this.level(), this, from, to, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), (entity -> canHitEntity(entity) && !(entity instanceof Player target && !ownerCanHarmPlayer(target)))
        );
    }

    /**
     * @return whether this projectile's owner can harm target player
     */
    protected boolean ownerCanHarmPlayer(Player player) {
        return !(this.getOwner() instanceof Player owner && !owner.canHarmPlayer(player));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(RAY_DMG, 0);
    }

    public void setDamage(int dmg) {
        this.entityData.set(RAY_DMG, dmg);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("ray_dmg", this.entityData.get(RAY_DMG));
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(RAY_DMG, compound.getInt("ray_dmg"));
    }

    public static Ray create(EntityType<Ray> rayEntityType, Level level) {
        return new Ray(rayEntityType, level);
    }
}
