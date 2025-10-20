package org.ageuxo.steelshako.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.block.ModBlocks;
import org.ageuxo.steelshako.block.ModFluids;
import org.ageuxo.steelshako.block.multi.MultiBlockType;
import org.ageuxo.steelshako.item.component.ChargeComponent;
import org.ageuxo.steelshako.item.component.ModComponents;

import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SteelShakoMod.MOD_ID);

    public static final DeferredItem<MiningRayGun> MINING_RAY_GUN = ITEMS.register("mining_ray_gun", ()-> new MiningRayGun(new Item.Properties()));
    public static final DeferredHolder<Item, RayPistol> RAY_PISTOL = ITEMS.register("ray_pistol", ()-> new RayPistol(new Item.Properties()));
    public static final DeferredItem<Item> VACUUM_TUBE = ITEMS.registerItem("vacuum_tube", Item::new);
    public static final DeferredItem<Item> CRYSTAL = ITEMS.registerItem("crystal", p -> new Item(p.component(ModComponents.CHARGE, new ChargeComponent(0, 32_000)).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));
    public static final DeferredItem<Item> INERT_CRYSTAL = ITEMS.registerItem("inert_crystal", Item::new);
    public static final DeferredItem<ItemNameBlockItem> GRUEL_SPORES = ITEMS.registerItem("gruel_spores", (p) -> new ItemNameBlockItem(ModBlocks.GRUEL_SHROOM.get(), p));

    public static final DeferredItem<DeployerItem> VAT_DEPLOYER = ITEMS.registerItem("vat_deployer", (p) -> new DeployerItem(p, MultiBlockType.GRUEL_VAT));
    public static final DeferredItem<DeployerItem> EXCITATION_DYNAMO_DEPLOYER = ITEMS.registerItem("excitation_dynamo_deployer", (p) -> new DeployerItem(p, MultiBlockType.EXCITATION_DYNAMO));

    public static final DeferredItem<BucketItem> GRUEL_BUCKET = ITEMS.registerItem("gruel_bucket", (p)-> new BucketItem(ModFluids.GRUEL.get(), p));
    public static final DeferredItem<BucketItem> MANGALAN_BUCKET = ITEMS.registerItem("mangalan_bucket", (p)-> new BucketItem(ModFluids.MANGALAN.get(), p));


    public static List<Item> getEntries() {
        return ITEMS.getEntries().stream()
                .filter(i-> !i.is(RAY_PISTOL.getId()))
                .map(i -> (Item)i.get())
                .toList();
    }

}
