package org.ageuxo.steelshako.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.ageuxo.steelshako.SteelShakoMod;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public record RayBeamPayload(int shooterId, Vector3f to) implements CustomPacketPayload {

    public static final Type<RayBeamPayload> TYPE = new Type<>(SteelShakoMod.modRL("ray_beam"));

    public static final StreamCodec<ByteBuf, RayBeamPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            RayBeamPayload::shooterId,
            ByteBufCodecs.VECTOR3F,
            RayBeamPayload::to,
            RayBeamPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
