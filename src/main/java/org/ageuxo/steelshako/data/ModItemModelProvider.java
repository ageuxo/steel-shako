package org.ageuxo.steelshako.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.item.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SteelShakoMod.MOD_ID, existingFileHelper);
        existingFileHelper.trackGenerated(ResourceLocation.withDefaultNamespace("builtin/entity"), MODEL);
    }

    @Override
    protected void registerModels() {

        basicItem(ModItems.GRUEL_BUCKET.get());
        basicItem(ModItems.MANGALAN_BUCKET.get());

        basicItem(ModItems.VACUUM_TUBE.get());

        deployerCrate("vat_deployer", "item/vat_deployer");
        deployerCrate("excitation_dynamo_deployer", "item/excitation_dynamo_deployer");

    }

    private void deployerCrate(String name, String frontPath) {
        withExistingParent(name, modLoc("block/deployer_crate"))
                .texture("front", modLoc(frontPath));
    }

}
