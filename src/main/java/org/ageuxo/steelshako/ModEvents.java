package org.ageuxo.steelshako;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import org.ageuxo.steelshako.block.be.ModBlockEntities;
import org.ageuxo.steelshako.block.be.DelegatingBlockEntity;
import org.ageuxo.steelshako.entity.Automaton;
import org.ageuxo.steelshako.entity.ModEntityTypes;

public class ModEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.VAT_PLACEHOLDER.get(), DelegatingBlockEntity::getFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.VAT_PLACEHOLDER.get(), DelegatingBlockEntity::getItemHandler);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.AUTOMATON.get(), Automaton.createAttributes().build());
    }

}
