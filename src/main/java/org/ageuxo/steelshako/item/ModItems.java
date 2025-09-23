package org.ageuxo.steelshako.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ageuxo.steelshako.SteelShakoMod;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(SteelShakoMod.MOD_ID);

    public static final DeferredHolder<Item, MiningRayGun> RAY_GUN = ITEMS.register("mining_ray_gun", ()-> new MiningRayGun(new Item.Properties()));

}
