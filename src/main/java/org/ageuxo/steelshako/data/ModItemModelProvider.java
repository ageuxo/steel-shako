package org.ageuxo.steelshako.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.item.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SteelShakoMod.MOD_ID, existingFileHelper);
        existingFileHelper.trackGenerated(ResourceLocation.withDefaultNamespace("builtin/entity"), MODEL);
    }

    @Override
    protected void registerModels() {

        filledBucket(ModItems.GRUEL_BUCKET, "item/gruel_bucket_mask");
        filledBucket(ModItems.MANGALAN_BUCKET, "item/mangalan_bucket_mask");

        basicItem(ModItems.VACUUM_TUBE.get());

    }

    private void filledBucket(DeferredItem<BucketItem> bucketItem, String maskPath) {
        withExistingParent(bucketItem.getId().toString(), mcLoc("item/generated"))
                .texture("layer1", modLoc(maskPath))
                .texture("layer0", mcLoc("item/bucket"));
    }
}
