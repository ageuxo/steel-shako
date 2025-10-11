package org.ageuxo.steelshako.render.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ageuxo.steelshako.SteelShakoMod;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, SteelShakoMod.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<VectorOption>> RAY_BEAM = register("ray_beam", false, VectorOption::codec, VectorOption::streamCodec);



    public static <T extends ParticleOptions> DeferredHolder<ParticleType<?>, ParticleType<T>> register(
            String name, boolean overrideLimiter, Function<ParticleType<T>, MapCodec<T>> codecGetter,
            Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter) {
        return PARTICLES.register(name, ()-> new ParticleType<T>(overrideLimiter) {
            @Override
            public @NotNull MapCodec<T> codec() {
                return codecGetter.apply(this);
            }

            @Override
            public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodecGetter.apply(this);
            }
        });
    }

}
