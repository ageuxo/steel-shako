package org.ageuxo.steelshako.item;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.block.ModFluids;
import org.ageuxo.steelshako.block.multi.MultiBlockType;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SteelShakoMod.MOD_ID);

    public static final DeferredHolder<Item, MiningRayGun> MINING_RAY_GUN = ITEMS.register("mining_ray_gun", ()-> new MiningRayGun(new Item.Properties()));
    public static final DeferredHolder<Item, RayPistol> RAY_PISTOL = ITEMS.register("ray_pistol", ()-> new RayPistol(new Item.Properties()));
    public static final DeferredItem<DeployerItem> VAT_DEPLOYER = ITEMS.registerItem("vat_deployer", (p) -> new DeployerItem(p, MultiBlockType.GRUEL_VAT));
    public static final DeferredItem<DeployerItem> EXCITATION_DYNAMO_DEPLOYER = ITEMS.registerItem("excitation_dynamo_deployer", (p) -> new DeployerItem(p, MultiBlockType.EXCITATION_DYNAMO));
    public static final DeferredItem<BucketItem> GRUEL_BUCKET = ITEMS.registerItem("gruel_bucket", (p)-> new BucketItem(ModFluids.GRUEL.get(), p));

}
