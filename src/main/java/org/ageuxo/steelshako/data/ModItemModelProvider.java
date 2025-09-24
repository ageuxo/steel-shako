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
        withExistingParent(ModItems.RAY_GUN.getId().toString(), "builtin/entity");
    }
}
