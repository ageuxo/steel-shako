package org.ageuxo.steelshako.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import org.ageuxo.steelshako.block.be.ExcitationDynamoBlockEntity;
import org.ageuxo.steelshako.render.geo.ExcitationDynamoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ExcitationDynamoRenderer extends GeoBlockRenderer<ExcitationDynamoBlockEntity> {

    public ExcitationDynamoRenderer(BlockEntityRendererProvider.Context ctx) {
        super(new ExcitationDynamoModel());
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
        super.rotateBlock(facing, poseStack);
    }
}
