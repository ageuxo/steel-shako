package org.ageuxo.steelshako;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.ageuxo.steelshako.entity.ModEntityTypes;
import org.ageuxo.steelshako.entity.render.RayRenderer;

@EventBusSubscriber(value = Dist.CLIENT, modid = SteelShakoMod.MOD_ID)
public class ClientEvents {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.RAY.get(), RayRenderer::new);
    }

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        event.register(RayRenderer.MODEL);
    }

}
