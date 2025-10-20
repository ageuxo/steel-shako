package org.ageuxo.steelshako.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.ageuxo.steelshako.ModDamageTypes;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.worldgen.ModBiomeModifiers;
import org.ageuxo.steelshako.worldgen.ModConfiguredFeatures;
import org.ageuxo.steelshako.worldgen.ModPlacements;

import java.util.List;
import java.util.Set;
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
                event.includeServer(), new ModTagsProvider.Entity(output, lookupProvider, SteelShakoMod.MOD_ID, fileHelper)
        );

        generator.addProvider(
                event.includeServer(), new ModTagsProvider.Biomes(output, lookupProvider, SteelShakoMod.MOD_ID, fileHelper)
        );

        generator.addProvider(
                event.includeServer(), new ModRecipeProvider(output, lookupProvider)
        );

        generator.addProvider(
                event.includeServer(), new ModBlockStateProvider(output, fileHelper)
        );

        generator.addProvider(
                event.includeServer(), new ModItemModelProvider(output, fileHelper)
        );

        generator.addProvider(
                event.includeServer(), new ModParticleDescriptionProvider(output, fileHelper)
        );

        generator.addProvider(
                event.includeServer(), (DataProvider.Factory<LootTableProvider>) (provider) -> new LootTableProvider(output, Set.of(),
                        List.of(
                                new LootTableProvider.SubProviderEntry(ModBlockLootSubProvider::new, LootContextParamSets.BLOCK),
                                new LootTableProvider.SubProviderEntry(ModEntityLootSubProvider::new, LootContextParamSets.ENTITY)
                        ),
                        lookupProvider
                )
        );

        // DataPack object provider
        generator.addProvider(
                event.includeServer(),
                (DataProvider.Factory<DatapackBuiltinEntriesProvider>) out -> new DatapackBuiltinEntriesProvider(out, lookupProvider, new RegistrySetBuilder()
                        .add(Registries.DAMAGE_TYPE, ModDamageTypes::addDamageTypes)
                        .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
                        .add(Registries.PLACED_FEATURE, ModPlacements::bootstrap)
                        .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap),
                        Set.of(SteelShakoMod.MOD_ID)
                )
        );
    }


}
