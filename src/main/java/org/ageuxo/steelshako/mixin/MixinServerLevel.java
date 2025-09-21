package org.ageuxo.steelshako.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.ageuxo.steelshako.attachment.MiningRayCache;
import org.ageuxo.steelshako.attachment.ModAttachments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel {

    @Inject(method = "tickChunk", at = @At("HEAD"))
    public void tickChunk(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        MiningRayCache rayCache = chunk.getData(ModAttachments.MINING_RAY_CACHE);
        if (!rayCache.isEmpty()) {
            rayCache.tickCooling();
        }
    }

}
