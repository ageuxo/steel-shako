package org.ageuxo.steelshako.menu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ageuxo.steelshako.SteelShakoMod;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, SteelShakoMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<BoilerMenu>> BOILER = MENUS.register("boiler", ()-> IMenuTypeExtension.create(BoilerMenu::new));

}
