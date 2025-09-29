package org.ageuxo.steelshako;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.ageuxo.steelshako.attachment.ModAttachments;
import org.ageuxo.steelshako.data.DataProviders;
import org.ageuxo.steelshako.entity.ModEntityTypes;
import org.ageuxo.steelshako.item.ModItems;
import org.ageuxo.steelshako.item.component.ModComponents;
import org.ageuxo.steelshako.network.ModPayloads;
import org.ageuxo.steelshako.render.particle.ModParticles;

@Mod(SteelShakoMod.MOD_ID)
public class SteelShakoMod {

    public static final String MOD_ID = "steel_shako";

    public SteelShakoMod(IEventBus modEventBus) {
        ModItems.ITEMS.register(modEventBus);
        ModAttachments.ATTACHMENTS.register(modEventBus);
        ModEntityTypes.ENTITIES.register(modEventBus);
        ModComponents.COMPONENTS.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);
        modEventBus.addListener(DataProviders::registerDataProviders);
        modEventBus.addListener(ModPayloads::register);
    }

    public static ResourceLocation modRL(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
