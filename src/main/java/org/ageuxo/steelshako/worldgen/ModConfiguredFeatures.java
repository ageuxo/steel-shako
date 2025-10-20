package org.ageuxo.steelshako.worldgen;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.block.ModBlocks;
import org.ageuxo.steelshako.block.ModFluids;
import org.ageuxo.steelshako.worldgen.feature.WallSupportConfiguration;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> MANGALAN_SPRING = createKey("mangalan_spring");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GRUELSHROOM_PATCH = createKey("gruelshroom_patch");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        ModConfiguredFeatures features = new ModConfiguredFeatures(context);
        features.registerFeatures();
    }

    private final BootstrapContext<ConfiguredFeature<?, ?>> context;

    private ModConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        this.context = context;
    }

    @SuppressWarnings("deprecation")
    public void registerFeatures() {
        register(MANGALAN_SPRING, Feature.SPRING,
                new SpringConfiguration(
                        ModFluids.MANGALAN.get().defaultFluidState(),
                        true,
                        4,
                        1,
                        HolderSet.direct(
                                Block::builtInRegistryHolder,
                                Blocks.DEEPSLATE,
                                Blocks.TUFF
                        )
                )
        );
        register(GRUELSHROOM_PATCH, ModFeatures.WALL_SUPPORT.get(),
                new WallSupportConfiguration(
                        4,
                        2,
                        ModBlocks.GRUEL_SHROOM,
                        new BlockMatchTest(ModBlocks.MANGALAN_FLUID.get())
                )
        );

    }

    protected <FC extends FeatureConfiguration, F extends Feature<FC>> void register(ResourceKey<ConfiguredFeature<?, ?>> configuredFeature, F feature, FC config) {
        this.context.register(configuredFeature, new ConfiguredFeature<>(feature, config));
    }

    protected static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, SteelShakoMod.modRL(name));
    }
}
