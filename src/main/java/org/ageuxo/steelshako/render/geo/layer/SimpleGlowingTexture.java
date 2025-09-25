package org.ageuxo.steelshako.render.geo.layer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.ageuxo.steelshako.mixin.AutoGlowingTextureAccessor;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;

public class SimpleGlowingTexture extends AutoGlowingTexture {
    public SimpleGlowingTexture(ResourceLocation originalLocation, ResourceLocation location) {
        super(originalLocation, location);
    }

    public static ResourceLocation getSuffixedEmissiveResource(ResourceLocation baseResource, String suffix) {
        ResourceLocation path = appendToPath(baseResource, suffix);

        generateTexture(path, textureManager -> textureManager.register(path, new SimpleGlowingTexture(baseResource, path)));

        return path;
    }

    public static RenderType getSuffixedRenderType(ResourceLocation texture, String suffix) {
        return AutoGlowingTextureAccessor.getGLOWING_RENDER_TYPE().apply(getSuffixedEmissiveResource(texture, suffix), false);
    }

    public static RenderType getSuffixedOutlineRenderType(ResourceLocation texture, String suffix) {
        return AutoGlowingTextureAccessor.getGLOWING_RENDER_TYPE().apply(getSuffixedEmissiveResource(texture, suffix), true);
    }



}
