package org.ageuxo.steelshako.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;
import org.ageuxo.steelshako.attachment.ModAttachments;

import java.util.Map;
import java.util.Set;

public class MiningRayProgressRenderer {

    public static void render(PoseStack poseStack, Camera camera) {
        poseStack.pushPose();
        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        Vec3 cameraPos = camera.getPosition();
        VertexConsumer buffer = bufferSource.getBuffer(ModRenderTypes.HEAT);

        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z); // Translate reverse view pos
        ChunkAccess cameraChunk = minecraft.level.getChunk(camera.getBlockPosition());
        ChunkPos camChunkPos = cameraChunk.getPos();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                Set<Map.Entry<BlockPos, Integer>> entrySet = minecraft.level.getChunk(camChunkPos.x + i, camChunkPos.z + j).getData(ModAttachments.MINING_RAY_CACHE).getEntrySet();

                for (var entry : entrySet) {
                    renderBlockHeat(poseStack, buffer, entry.getKey(), entry.getValue());
                }
            }
        }

        bufferSource.endBatch(ModRenderTypes.HEAT);
        poseStack.popPose();
    }

    public static void renderBlockHeat(PoseStack poseStack, VertexConsumer buffer, BlockPos pos, int heat) {
        poseStack.pushPose();
        PoseStack.Pose pose = poseStack.last();
        poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
        poseStack.scale(1.02f, 1.02f, 1.02f); // Make it larger than the enclosed block

        float red = 1, green = 0, blue = 0, alpha = (float) heat / 100f;


        float startX = 0, startY = 0, startZ = 0, endX = 1, endY = 1, endZ = 1;

        //down
        buffer.addVertex(pose, startX, startY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, startY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, startY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, startY, endZ).setColor(red, green, blue, alpha);

        //up
        buffer.addVertex(pose, startX, endY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, endY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, endY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, endY, startZ).setColor(red, green, blue, alpha);

        //east
        buffer.addVertex(pose, startX, startY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, endY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, endY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, startY, startZ).setColor(red, green, blue, alpha);

        //west
        buffer.addVertex(pose, startX, startY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, startY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, endY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, endY, endZ).setColor(red, green, blue, alpha);

        //south
        buffer.addVertex(pose, endX, startY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, endY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, endY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, startY, endZ).setColor(red, green, blue, alpha);

        //north
        buffer.addVertex(pose, startX, startY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, startY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, endY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, endY, startZ).setColor(red, green, blue, alpha);


        poseStack.popPose();
    }






}
