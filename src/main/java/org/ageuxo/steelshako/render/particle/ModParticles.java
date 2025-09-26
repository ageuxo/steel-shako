package org.ageuxo.steelshako.render.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ageuxo.steelshako.SteelShakoMod;

public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, SteelShakoMod.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MINING_RAY_BEAM = PARTICLES.register("mining_ray_beam", ()-> new SimpleParticleType(false));

}
