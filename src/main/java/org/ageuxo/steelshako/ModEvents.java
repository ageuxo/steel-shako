package org.ageuxo.steelshako;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.ageuxo.steelshako.block.be.ModBlockEntities;
import org.ageuxo.steelshako.block.be.VatPlaceholderBlockEntity;

public class ModEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.VAT_PLACEHOLDER.get(), VatPlaceholderBlockEntity::getFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.VAT_PLACEHOLDER.get(), VatPlaceholderBlockEntity::getItemHandler);
    }
}
