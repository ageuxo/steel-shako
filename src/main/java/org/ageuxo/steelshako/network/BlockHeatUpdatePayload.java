package org.ageuxo.steelshako.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.ageuxo.steelshako.SteelShakoMod;
import org.jetbrains.annotations.NotNull;

public record BlockHeatUpdatePayload(BlockPos pos, int heat) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BlockHeatUpdatePayload> TYPE = new Type<>(SteelShakoMod.modRL("block_heat_update"));

    public static final StreamCodec<ByteBuf, BlockHeatUpdatePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            BlockHeatUpdatePayload::pos,
            ByteBufCodecs.VAR_INT,
            BlockHeatUpdatePayload::heat,
            BlockHeatUpdatePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
