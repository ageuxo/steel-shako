package org.ageuxo.steelshako.render.geo.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.function.Predicate;

public class PredicatedGlowingGeoLayer<T extends GeoAnimatable> extends SimpleGlowingGeoLayer<T> {

    protected final Predicate<T> shouldGlow;

    public PredicatedGlowingGeoLayer(GeoRenderer<T> renderer, String textureSuffix, Predicate<T> shouldGlow) {
        super(renderer, textureSuffix);
        this.shouldGlow = shouldGlow;
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (this.shouldGlow.test(animatable)){
            renderType = getRenderType(animatable, bufferSource);

            if (renderType != null) {
                getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, renderType,
                        bufferSource.getBuffer(renderType), partialTick, LightTexture.FULL_SKY, packedOverlay,
                        getRenderer().getRenderColor(animatable, partialTick, packedLight).argbInt());
            }
        }
    }
}
