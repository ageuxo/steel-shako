package org.ageuxo.steelshako.render.geo.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.util.ClientUtil;

public class SimpleGlowingGeoLayer<T extends GeoAnimatable> extends AutoGlowingGeoLayer<T> {
    protected final String textureSuffix;

    public SimpleGlowingGeoLayer(GeoRenderer<T> renderer, String textureSuffix) {
        super(renderer);
        this.textureSuffix = textureSuffix;
    }

    @Override
    protected @Nullable RenderType getRenderType(T animatable, @Nullable MultiBufferSource bufferSource) {
        if (!(animatable instanceof Entity entity))
            return SimpleGlowingTexture.getSuffixedRenderType(getTextureResource(animatable), this.textureSuffix);

        boolean invisible = entity.isInvisible();
        ResourceLocation texture = SimpleGlowingTexture.getSuffixedEmissiveResource(getTextureResource(animatable), this.textureSuffix);

        if (invisible && !entity.isInvisibleTo(ClientUtil.getClientPlayer()))
            return RenderType.itemEntityTranslucentCull(texture);

        if (Minecraft.getInstance().shouldEntityAppearGlowing(entity)) {
            if (invisible)
                return RenderType.outline(texture);

            return SimpleGlowingTexture.getSuffixedOutlineRenderType(getTextureResource(animatable), this.textureSuffix);
        }

        return invisible ? null : SimpleGlowingTexture.getSuffixedRenderType(getTextureResource(animatable), this.textureSuffix);
    }


}
