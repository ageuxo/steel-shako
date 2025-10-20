package org.ageuxo.steelshako;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.ageuxo.steelshako.attachment.ModAttachments;
import org.ageuxo.steelshako.block.ModBlocks;
import org.ageuxo.steelshako.block.ModFluids;
import org.ageuxo.steelshako.block.be.ModBlockEntities;
import org.ageuxo.steelshako.data.DataProviders;
import org.ageuxo.steelshako.entity.ModEntityTypes;
import org.ageuxo.steelshako.item.ModItems;
import org.ageuxo.steelshako.item.component.ModComponents;
import org.ageuxo.steelshako.menu.ModMenuTypes;
import org.ageuxo.steelshako.network.ModPayloads;
import org.ageuxo.steelshako.render.particle.ModParticles;

@Mod(SteelShakoMod.MOD_ID)
public class SteelShakoMod {

    public static final String MOD_ID = "steel_shako";

    public SteelShakoMod(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModAttachments.ATTACHMENTS.register(modEventBus);
        ModEntityTypes.ENTITIES.register(modEventBus);
        ModComponents.COMPONENTS.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);
        ModFluids.FLUID_TYPES.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        modEventBus.addListener(DataProviders::registerDataProviders);
        modEventBus.addListener(ModPayloads::register);
        modEventBus.addListener(ModFluids::registerFluids);
        modEventBus.register(ModEvents.class);
    }

    public static ResourceLocation modRL(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
