package org.ageuxo.steelshako.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.render.particle.ModParticles;
import org.ageuxo.steelshako.render.particle.VectorOption;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.Supplier;

public record RayBeamPayload(int shooterId, Vector3f to, Colour colour) implements CustomPacketPayload {

    public static final Type<RayBeamPayload> TYPE = new Type<>(SteelShakoMod.modRL("ray_beam"));

    public static final StreamCodec<ByteBuf, RayBeamPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            RayBeamPayload::shooterId,
            ByteBufCodecs.VECTOR3F,
            RayBeamPayload::to,
            Colour.STREAM_CODEC,
            RayBeamPayload::colour,
            RayBeamPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public enum Colour {
        RED(ModParticles.RED_RAY_BEAM),
        ORANGE(ModParticles.ORANGE_RAY_BEAM);

        public static final StreamCodec<ByteBuf, Colour> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(Colour::valueOf, Colour::name);

        private final Supplier<ParticleType<VectorOption>> supplier;

        Colour(Supplier<ParticleType<VectorOption>> supplier) {
            this.supplier = supplier;
        }

        public ParticleType<VectorOption> get() {
            return supplier.get();
        }
    }

}
