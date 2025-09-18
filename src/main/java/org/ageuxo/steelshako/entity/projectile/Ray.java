package org.ageuxo.steelshako.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.ageuxo.steelshako.entity.ModEntityTypes;
import org.jetbrains.annotations.NotNull;

public class Ray extends Projectile {
    public static EntityDataAccessor<Integer> RAY_DMG = SynchedEntityData.defineId(Ray.class, EntityDataSerializers.INT);

    protected Ray(EntityType<Ray> entityType, Level level) {
        super(entityType, level);
    }

    public Ray(Level level) {
        this(ModEntityTypes.RAY.get(), level);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 pos = this.position();
        Vec3 deltaMove = this.getDeltaMovement();

        Vec3 futurePos = pos.add(deltaMove);
        // clip to block
        HitResult hitResult = this.level().clip(new ClipContext(pos, futurePos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

        // set futurePos to collision point
        if (hitResult.getType() != HitResult.Type.MISS) {
            futurePos = hitResult.getLocation();
        }

        while (!this.isRemoved() && hitResult != null) {
            // find entity between currentPos and futurePos
            EntityHitResult entityHitResult = findHitEntity(pos, futurePos);
            if (entityHitResult != null) {
                hitResult = entityHitResult;
            }

            if (entityHitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
                Entity hitEntity = entityHitResult.getEntity();
                if (hitEntity instanceof Player target && !ownerCanHarmPlayer(target)) {
                    hitResult = null;
                    entityHitResult = null;
                }
            }

            if (hitResult != null && hitResult.getType() != HitResult.Type.MISS && !this.isNoGravity()) {
                if (EventHooks.onProjectileImpact(this, hitResult)) break;
                ProjectileDeflection deflection = this.hitTargetOrDeflectSelf(hitResult);
                this.hasImpulse = true; // AbstractArrow does this, so we'd better
                if (deflection != ProjectileDeflection.NONE) {
                    break;
                }
            }

            if (entityHitResult == null ) break;

            hitResult = null;
        }

        if (!this.noPhysics){
            // set position to new position
            this.setPos(pos.add(this.getDeltaMovement()));
            ProjectileUtil.rotateTowardsMovement(this, 0.2f);
        }
    }

    protected EntityHitResult findHitEntity(Vec3 from, Vec3 to) {
        return ProjectileUtil.getEntityHitResult(
                this.level(), this, from, to, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), this::canHitEntity
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
