package org.ageuxo.steelshako.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import org.ageuxo.steelshako.block.be.ExcitationDynamoBlockEntity;
import org.ageuxo.steelshako.render.geo.ExcitationDynamoModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExcitationDynamoRenderer extends GeoBlockRenderer<ExcitationDynamoBlockEntity> {

    public ExcitationDynamoRenderer(BlockEntityRendererProvider.Context ctx) {
        super(new ExcitationDynamoModel());
    }

    @Override
    public void preRender(PoseStack poseStack, ExcitationDynamoBlockEntity animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {

        if (animatable.getChargeSlot().isEmpty()) {
            model.getBone("crystal").ifPresent(b-> b.setHidden(true));
        } else {
            model.getBone("crystal").ifPresent(b-> b.setHidden(false));
        }

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(ExcitationDynamoBlockEntity blockEntity) {
        return blockEntity.renderBounds();
    }
}
