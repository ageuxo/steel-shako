package org.ageuxo.steelshako.render.geo.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import org.ageuxo.steelshako.item.MiningRayGun;
import org.ageuxo.steelshako.render.geo.MiningRayGunRenderer;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class WarmUpGlowingGeoLayer extends SimpleGlowingGeoLayer<MiningRayGun> {
    private final MiningRayGunRenderer renderer;

    public WarmUpGlowingGeoLayer(MiningRayGunRenderer renderer, String textureSuffix) {
        super(renderer, textureSuffix);
        this.renderer = renderer;
    }

    @Override
    public void render(PoseStack poseStack, MiningRayGun animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        renderType = RenderType.entityTranslucent(SimpleGlowingTexture.getSuffixedEmissiveResource(getTextureResource(animatable), "_tubes"));

        float fraction = (float) renderer.getRampUp() / MiningRayGun.RAMPUP_TIME;
        float minnedFraction = Math.min(1, fraction);

        float a = renderer.hasCharge() ? minnedFraction * 15 : 0;
        int packedGlow = fraction >= 1 ? LightTexture.FULL_BRIGHT : LightTexture.pack((int) a, Math.min(14, LightTexture.sky(packedLight)));
        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, renderType,
                bufferSource.getBuffer(renderType), partialTick, packedGlow, packedOverlay,
                FastColor.ARGB32.color(200, 255, 255, 255));

    }




}

