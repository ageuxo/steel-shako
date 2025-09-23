package org.ageuxo.steelshako;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> MINING_RAY = ResourceKey.create(Registries.DAMAGE_TYPE, SteelShakoMod.modRL("mining_ray"));

    public static void addDamageTypes(BootstrapContext<DamageType> ctx) {
        ctx.register(ModDamageTypes.MINING_RAY, new DamageType(ModDamageTypes.MINING_RAY.location().getPath(),
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
                0.12f,
                DamageEffects.HURT,
                DeathMessageType.DEFAULT));
    }
}
