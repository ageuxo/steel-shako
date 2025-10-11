package org.ageuxo.steelshako.network;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.ageuxo.steelshako.attachment.MiningRayCache;
import org.ageuxo.steelshako.attachment.ModAttachments;
import org.ageuxo.steelshako.render.particle.ModParticles;
import org.ageuxo.steelshako.render.particle.VectorOption;
import org.joml.Vector3f;

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

    public static void handleRayBeamPayload(RayBeamPayload payload, IPayloadContext ctx) {
        if (!addRayParticle(payload.shooterId(), payload.to())) {
            LogUtils.getLogger().error("Received RayBeamPayload for id of non-existent entity! These should only ever be sent for tracked entities!");
        }
    }

    public static boolean addRayParticle(int shooterId, Vector3f to) {
        ClientLevel level = Minecraft.getInstance().level;
        //noinspection DataFlowIssue
        Entity shooter = level.getEntity(shooterId);
        if (shooter != null){
            Vec3 from = shooter.getEyePosition().add(
                    shooter.getViewVector(0)
                            .yRot(-25f * Mth.DEG_TO_RAD)
            ).add(0, -0.4, 0);

            level.addParticle(new VectorOption(ModParticles.RAY_BEAM.get(), to), from.x, from.y, from.z, 0, 0, 0);
            return true;
        }
        return false;
    }

}
