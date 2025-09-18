package org.ageuxo.steelshako.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.entity.projectile.Ray;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class RayRenderer extends EntityRenderer<Ray> {
    public static final ResourceLocation TEXTURE = SteelShakoMod.modRL("textures/block/projectiles/ray.png");
    public static final ModelResourceLocation MODEL = ModelResourceLocation.standalone(SteelShakoMod.modRL("projectile/ray"));
    private final ModelBlockRenderer modelRenderer;
    private final BakedModel rayModel;

    public RayRenderer(EntityRendererProvider.Context context) {
        super(context);
        BlockRenderDispatcher blockRenderDispatcher = context.getBlockRenderDispatcher();
        this.modelRenderer = blockRenderDispatcher.getModelRenderer();
        this.rayModel = context.getModelManager().getModel(MODEL);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Ray entity) {
        return TEXTURE;
    }

    @Override
    public void render(@NotNull Ray ray, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.cutout());
        poseStack.pushPose();
        poseStack.mulPose(lerpedRotation(Axis.YP, ray.yRotO, ray.getYRot(), partialTick));
        poseStack.mulPose(lerpedRotation(Axis.XP, ray.xRotO, ray.getXRot(), partialTick));
        poseStack.translate(-0.5f, 0.0f, -0.5f);
        modelRenderer.renderModel(poseStack.last(), consumer, null, rayModel, 1f, 1f, 1f, packedLight, 0, ModelData.EMPTY, RenderType.cutout());

        poseStack.popPose();
    }

    protected Quaternionf lerpedRotation(Axis axis, float rotA, float rotB, float partialTicks) {
        return axis.rotationDegrees(Mth.lerp(partialTicks, rotA, rotB));
    }

}
