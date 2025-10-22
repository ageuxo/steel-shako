package org.ageuxo.steelshako.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.item.component.ChargeComponent;
import org.ageuxo.steelshako.item.component.ModComponents;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SteelShakoMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.INERT_CRYSTAL.get()))
                    .title(Component.translatable("itemGroup.steel_shako.main"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.VACUUM_TUBE);
                        output.accept(ModItems.INERT_CRYSTAL);
                        output.accept(ModItems.CRYSTAL);
                        ItemStack chargedCrystal = new ItemStack(ModItems.CRYSTAL.get());
                        chargedCrystal.set(ModComponents.CHARGE, new ChargeComponent(32_000, 32_000));
                        output.accept(chargedCrystal);
                        output.accept(ModItems.MINING_RAY_GUN);
                        output.accept(ModItems.VAT_DEPLOYER);
                        output.accept(ModItems.EXCITATION_DYNAMO_DEPLOYER);
                        output.accept(ModItems.GRUEL_SPORES);
                        output.accept(ModItems.GRUEL_BUCKET);
                        output.accept(ModItems.MANGALAN_BUCKET);
                    })
                    .build()
            );
}
