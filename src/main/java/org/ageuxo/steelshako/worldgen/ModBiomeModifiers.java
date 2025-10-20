package org.ageuxo.steelshako.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.ageuxo.steelshako.ModTags;
import org.ageuxo.steelshako.SteelShakoMod;

public class ModBiomeModifiers {

    public static final ResourceKey<BiomeModifier> HAS_MANGALAN_SPRINGS = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, SteelShakoMod.modRL("has_mangalan_springs"));
    public static final ResourceKey<BiomeModifier> HAS_GRUELSHROOM_PATCHES = ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, SteelShakoMod.modRL("has_gruelshroom_patches"));


    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        context.register(HAS_MANGALAN_SPRINGS,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(ModTags.HAS_MANGALAN_SPRINGS),
                        HolderSet.direct(placedFeatures.getOrThrow(ModPlacements.MANGALAN_SPRING)),
                        GenerationStep.Decoration.FLUID_SPRINGS
                )
        );
        context.register(HAS_GRUELSHROOM_PATCHES,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(ModTags.HAS_MANGALAN_SPRINGS),
                        HolderSet.direct(placedFeatures.getOrThrow(ModPlacements.GRUELSHROOM_PATCH)),
                        GenerationStep.Decoration.VEGETAL_DECORATION
                )
        );
    }

}
