package org.ageuxo.steelshako.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ageuxo.steelshako.SteelShakoMod;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SteelShakoMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.INERT_CRYSTAL.get()))
                    .title(Component.translatable("itemGroup.steel_shako.main"))
                    .displayItems((parameters, output) -> {
                        ModItems.getEntries().forEach(output::accept);
                    })
                    .build()
            );
}
