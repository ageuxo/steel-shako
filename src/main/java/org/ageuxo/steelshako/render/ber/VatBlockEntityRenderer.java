package org.ageuxo.steelshako.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.ageuxo.steelshako.block.ModBlocks;
import org.ageuxo.steelshako.block.be.VatBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class VatBlockEntityRenderer implements BlockEntityRenderer<VatBlockEntity> {

    public static final Vector3f XP = new Vector3f(1, 0, 0);
    public static final Vector3f XN = new Vector3f(-1, 0, 0);
    public static final Vector3f YP = new Vector3f(0, 1, 0);
    public static final Vector3f YN = new Vector3f(0, -1, 0);
    public static final Vector3f ZP = new Vector3f(0, 0, 1);
    public static final Vector3f ZN = new Vector3f(0, 0, -1);

    public VatBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(VatBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        FluidState fluidState = ModBlocks.GRUEL_FLUID.get().fluid.defaultFluidState();
        RenderType chunkRenderType = ItemBlockRenderTypes.getRenderLayer(fluidState);
        FluidTank slopTank = blockEntity.slopTank();
        float fillLevel = (float) slopTank.getFluidAmount() / slopTank.getCapacity();
        BlockPos pos = blockEntity.getBlockPos();
        Level level = Objects.requireNonNull(blockEntity.getLevel());
//        int lightColor = LevelRenderer.getLightColor(level, pos.offset(0, 4, 0));
        TextureAtlasSprite[] fluidSprites = FluidSpriteCache.getFluidSprites(level, pos, fluidState);
        TextureAtlasSprite still = fluidSprites[0];
        TextureAtlasSprite flowing = fluidSprites[1];

        if (fillLevel > 0){
            poseStack.pushPose();
            poseStack.translate(0.5f, 2, 0.5f);
            addSlop(poseStack.last(), chunkRenderType, bufferSource, packedLight, still, flowing, fillLevel);

            poseStack.popPose();
        }

    }

    public void addSlop(PoseStack.Pose pose, RenderType chunkRenderType, MultiBufferSource bufferSource, int packedLight, TextureAtlasSprite still, TextureAtlasSprite flowing, float fillLevel) {
        RenderType entityRenderType = RenderTypeHelper.getEntityRenderType(chunkRenderType, false);
        VertexConsumer buffer = bufferSource.getBuffer(entityRenderType);

        float w = 1.45f;
        float h = 0.95f * fillLevel;
        float b = 0.8f;

        // Front (+Z)
        addQuad(pose, buffer, -w, -b, w,
                w, -b, w,
                w, h, w,
                -w, h, w,
                packedLight, flowing, ZP);

        // Back (-Z)
        addQuad(pose, buffer, w, -b, -w,
                -w, -b, -w,
                -w, h, -w,
                w, h, -w,
                packedLight, flowing, ZN);

        // Left (-X)
        addQuad(pose, buffer, -w, -b, -w,
                -w, -b, w,
                -w, h, w,
                -w, h, -w,
                packedLight, flowing, XN);

        // Right (+X)
        addQuad(pose, buffer, w, -b, w,
                w, -b, -w,
                w, h, -w,
                w, h, w,
                packedLight, flowing, XP);

        // Top (+Y)
        addQuad(pose, buffer, -w, h, w,
                w, h, w,
                w, h, -w,
                -w, h, -w,
                packedLight, still, YP);
    }

    private void addQuad(PoseStack.Pose pose, VertexConsumer buffer,
                         float x1, float y1, float z1,
                         float x2, float y2, float z2,
                         float x3, float y3, float z3,
                         float x4, float y4, float z4, int packedLight, TextureAtlasSprite sprite, Vector3f normal) {
        addVertex(pose, buffer, x1, y1, z1, sprite.getU0(), sprite.getV0(), packedLight, normal);
        addVertex(pose, buffer, x2, y2, z2, sprite.getU1(), sprite.getV0(), packedLight, normal);
        addVertex(pose, buffer, x3, y3, z3, sprite.getU1(), sprite.getV1(), packedLight, normal);
        addVertex(pose, buffer, x4, y4, z4, sprite.getU0(), sprite.getV1(), packedLight, normal);
    }

    private void addVertex(PoseStack.Pose pose, VertexConsumer buffer, float x, float y, float z, float u, float v, int packedLight, Vector3f normal) {
        Vector4f pos = new Vector4f(x, y, z, 1f);
        buffer.addVertex(pose, pos.x(), pos.y(), pos.z())
                .setUv(u, v)
                .setColor(255, 255, 255, 255)
                .setNormal(pose, normal.x, normal.y, normal.z)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight);
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(VatBlockEntity blockEntity) {
        return blockEntity.renderBounds();
    }
}
