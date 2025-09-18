package org.ageuxo.steelshako.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ageuxo.steelshako.SteelShakoMod;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(SteelShakoMod.MOD_ID);

    public static final DeferredHolder<Item, RayGun> RAY_GUN = ITEMS.register("ray_gun", ()-> new RayGun(new Item.Properties()));

}
