package org.ageuxo.steelshako.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.ageuxo.steelshako.attachment.MiningRayCache;
import org.ageuxo.steelshako.attachment.ModAttachments;

public class ClientPayloadHandler {

    public static void handleBlockHeatUpdatePayload(BlockHeatUpdatePayload payload, IPayloadContext ctx) {
        BlockPos pos = payload.pos();
        int heat = payload.heat();
        //noinspection DataFlowIssue
        ChunkAccess chunk = Minecraft.getInstance().level.getChunk(pos);
        MiningRayCache rayCache = chunk.getData(ModAttachments.MINING_RAY_CACHE);
        rayCache.updateFromServer(pos, heat);
        chunk.setData(ModAttachments.MINING_RAY_CACHE, rayCache);
    }

}
