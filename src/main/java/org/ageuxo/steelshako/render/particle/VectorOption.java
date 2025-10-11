package org.ageuxo.steelshako.render.particle;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class VectorOption implements ParticleOptions {

    public static MapCodec<VectorOption> codec(ParticleType<? extends VectorOption> type) {
        return ExtraCodecs.VECTOR3F.xmap((vec) -> new VectorOption(type, vec), VectorOption::vector).fieldOf("vector");
    }

    public static StreamCodec<ByteBuf, VectorOption> streamCodec(ParticleType<? extends VectorOption> type) {
        return ByteBufCodecs.VECTOR3F.map((vec) -> new VectorOption(type, vec), VectorOption::vector);
    }

    private final ParticleType<? extends VectorOption> type;
    private final Vector3f vector;

    public VectorOption(ParticleType<? extends VectorOption> type, Vector3f vector) {
        this.type = type;
        this.vector = vector;
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return type;
    }

    public Vector3f vector() {
        return vector;
    }
}
