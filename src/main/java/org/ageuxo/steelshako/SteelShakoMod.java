package org.ageuxo.steelshako;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(SteelShakoMod.MOD_ID)
public class SteelShakoMod {

    public static final String MOD_ID = "steel_shako";

    public SteelShakoMod(IEventBus modEventBus) {

    }

    public static ResourceLocation modRL(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
