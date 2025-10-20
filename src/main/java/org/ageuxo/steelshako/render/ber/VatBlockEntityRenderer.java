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
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.ageuxo.steelshako.block.ModBlocks;
import org.ageuxo.steelshako.block.be.VatBlockEntity;
import org.ageuxo.steelshako.render.ColourUtils;
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

    public static final int GRUEL_TINT = 0xC9BB9A;

    public VatBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(VatBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        FluidState gruelFluid = ModBlocks.GRUEL_FLUID.get().fluid.defaultFluidState();
        FluidState waterFluid = Fluids.WATER.defaultFluidState();
        RenderType chunkRenderType = ItemBlockRenderTypes.getRenderLayer(gruelFluid);


        BlockPos pos = blockEntity.getBlockPos();
        Vec3 center = blockEntity.getBlockPos().getCenter();
        double xOffset = center.x - pos.getX();
        double zOffset = center.z - pos.getZ();

        FluidTank slopTank = blockEntity.slopTank();
        float fillLevel = (float) slopTank.getFluidAmount() / slopTank.getCapacity();
        float cookLevel = 8000f / slopTank.getCapacity();
        float progress = (float) blockEntity.progress() / VatBlockEntity.PROCESS_TIME;
        Level level = Objects.requireNonNull(blockEntity.getLevel());

        TextureAtlasSprite[] gruelSprites = FluidSpriteCache.getFluidSprites(level, pos, gruelFluid);
        TextureAtlasSprite gruelStill = gruelSprites[0];
        TextureAtlasSprite gruelFlowing = gruelSprites[1];

        TextureAtlasSprite[] waterSprites = FluidSpriteCache.getFluidSprites(level, pos, waterFluid);
        TextureAtlasSprite waterStill = waterSprites[0];
        TextureAtlasSprite waterFlowing = waterSprites[1];

        poseStack.pushPose();
        poseStack.translate(xOffset, 1, zOffset);

        // Height of vat
        float fullHeight = 1.98f;

        float fillHeight = fullHeight * fillLevel;
        if (fillLevel > 0){
            addVatLiquid(poseStack.last(), chunkRenderType, bufferSource, packedLight, gruelStill, gruelFlowing, fillHeight, 255, 255, 255, 255);
        }

        if (progress > 0) {

            poseStack.translate(0, fillHeight, 0);
            int packedTint = IClientFluidTypeExtensions.of(waterFluid).getTintColor();
            int lerped = ColourUtils.colourLerp(progress, packedTint, GRUEL_TINT);
            int red = (lerped >> 16) & 0xFF;
            int blue = (lerped >> 8) & 0xFF;
            int green = lerped & 0xFF;
            addVatLiquid(poseStack.last(), chunkRenderType, bufferSource, packedLight, waterStill, waterFlowing, fullHeight * cookLevel, red, green, blue, 255);

        }

        poseStack.popPose();
    }

    public static void addVatLiquid(PoseStack.Pose pose, RenderType chunkRenderType, MultiBufferSource bufferSource, int packedLight, TextureAtlasSprite still, TextureAtlasSprite flowing, float liquidHeight, int red, int green, int blue, int alpha) {
        RenderType entityRenderType = RenderTypeHelper.getEntityRenderType(chunkRenderType, false);
        VertexConsumer buffer = bufferSource.getBuffer(entityRenderType);

        float width = 1.45f;

        // Front (+Z)
        addQuad(pose, buffer, -width, 0.0f, width,
                width, 0.0f, width,
                width, liquidHeight, width,
                -width, liquidHeight, width,
                packedLight, flowing, ZP, red, green, blue, alpha);

        // Back (-Z)
        addQuad(pose, buffer, width, 0.0f, -width,
                -width, 0.0f, -width,
                -width, liquidHeight, -width,
                width, liquidHeight, -width,
                packedLight, flowing, ZN, red, green, blue, alpha);

        // Left (-X)
        addQuad(pose, buffer, -width, 0.0f, -width,
                -width, 0.0f, width,
                -width, liquidHeight, width,
                -width, liquidHeight, -width,
                packedLight, flowing, XN, red, green, blue, alpha);

        // Right (+X)
        addQuad(pose, buffer, width, 0.0f, width,
                width, 0.0f, -width,
                width, liquidHeight, -width,
                width, liquidHeight, width,
                packedLight, flowing, XP, red, green, blue, alpha);

        // Top (+Y)
        addQuad(pose, buffer, -width, liquidHeight, width,
                width, liquidHeight, width,
                width, liquidHeight, -width,
                -width, liquidHeight, -width,
                packedLight, still, YP, red, green, blue, alpha);
    }

    public static void addQuad(PoseStack.Pose pose, VertexConsumer buffer,
                         float x1, float y1, float z1,
                         float x2, float y2, float z2,
                         float x3, float y3, float z3,
                         float x4, float y4, float z4, int packedLight, TextureAtlasSprite sprite, Vector3f normal, int red, int green, int blue, int alpha) {
        addVertex(pose, buffer, x1, y1, z1, sprite.getU0(), sprite.getV0(), packedLight, normal, red, green, blue, alpha);
        addVertex(pose, buffer, x2, y2, z2, sprite.getU1(), sprite.getV0(), packedLight, normal, red, green, blue, alpha);
        addVertex(pose, buffer, x3, y3, z3, sprite.getU1(), sprite.getV1(), packedLight, normal, red, green, blue, alpha);
        addVertex(pose, buffer, x4, y4, z4, sprite.getU0(), sprite.getV1(), packedLight, normal, red, green, blue, alpha);
    }

    public static void addVertex(PoseStack.Pose pose, VertexConsumer buffer, float x, float y, float z, float u, float v, int packedLight, Vector3f normal, int red, int green, int blue, int alpha) {
        Vector4f pos = new Vector4f(x, y, z, 1f);
        buffer.addVertex(pose, pos.x(), pos.y(), pos.z())
                .setUv(u, v)
                .setColor(red, green, blue, alpha)
                .setNormal(pose, normal.x, normal.y, normal.z)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight);
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(VatBlockEntity blockEntity) {
        return blockEntity.renderBounds();
    }
}
