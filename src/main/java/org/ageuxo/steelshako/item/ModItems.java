package org.ageuxo.steelshako.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.block.multi.MultiBlockType;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SteelShakoMod.MOD_ID);

    public static final DeferredHolder<Item, MiningRayGun> RAY_GUN = ITEMS.register("mining_ray_gun", ()-> new MiningRayGun(new Item.Properties()));
    public static final DeferredItem<DeployerItem> VAT_DEPLOYER = ITEMS.registerItem("vat_deployer", (p) -> new DeployerItem(p, MultiBlockType.GRUEL_VAT));

}
