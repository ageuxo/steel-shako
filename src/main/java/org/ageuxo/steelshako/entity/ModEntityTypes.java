package org.ageuxo.steelshako.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.entity.projectile.Ray;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, SteelShakoMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<Ray>> RAY = register("ray", EntityType.Builder.of(Ray::create, MobCategory.MISC)
            .sized(0.4F, 0.4F)
            .eyeHeight(0.13F)
            .clientTrackingRange(10)
    );

    public static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        return ENTITIES.register(name, ()-> builder.build(name));
    }
}
