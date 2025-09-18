package org.ageuxo.steelshako.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.ageuxo.steelshako.SteelShakoMod;

import java.util.concurrent.CompletableFuture;

public class DataProviders {

    public static void registerDataProviders(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        ModTagsProvider.Block blockProvider = generator.addProvider(
                event.includeServer(), new ModTagsProvider.Block(output, lookupProvider, SteelShakoMod.MOD_ID, fileHelper)
        );

        generator.addProvider(
                event.includeServer(), new ModTagsProvider.Item(output, lookupProvider, blockProvider.contentsGetter(), SteelShakoMod.MOD_ID, fileHelper)
        );

        generator.addProvider(
                event.includeServer(), new ModItemModelProvider(output, fileHelper)
        );

    }
}
