package org.ageuxo.steelshako.mixin;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;

import java.util.function.BiFunction;

@Mixin(AutoGlowingTexture.class)
public interface AutoGlowingTextureAccessor {
    @Accessor
    static BiFunction<ResourceLocation, Boolean, RenderType> getGLOWING_RENDER_TYPE() {
        throw new UnsupportedOperationException();
    }
}
