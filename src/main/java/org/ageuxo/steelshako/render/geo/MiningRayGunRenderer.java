package org.ageuxo.steelshako.render.geo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.ageuxo.steelshako.item.MiningRayGun;
import org.ageuxo.steelshako.item.component.ModComponents;
import org.ageuxo.steelshako.render.geo.layer.PredicatedGlowingGeoLayer;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MiningRayGunRenderer extends GeoItemRenderer<MiningRayGun> {

    private int rampUp;

    public MiningRayGunRenderer() {
        super(new MiningRayModel());
        addRenderLayer(new PredicatedGlowingGeoLayer<>(this, "_crystal", a -> a.getCharge(getCurrentItemStack()) > 0));
        addRenderLayer(new PredicatedGlowingGeoLayer<>(this, "_tube_a", a -> this.rampUp > 1)); // Right back tube
        addRenderLayer(new PredicatedGlowingGeoLayer<>(this, "_tube_b", a -> this.rampUp > 10)); // Right fore tube
        addRenderLayer(new PredicatedGlowingGeoLayer<>(this, "_tube_c", a -> this.rampUp > 15)); // Left fore tube
        addRenderLayer(new PredicatedGlowingGeoLayer<>(this, "_tube_d", a -> this.rampUp > 25)); // Left back tube
    }

    public int getRampUp() {
        return getCurrentItemStack().getOrDefault(ModComponents.RAY_RAMPUP.get(), 0);
    }

    @Override
    public void preRender(PoseStack poseStack, MiningRayGun animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        this.rampUp = getRampUp();
    }
}
