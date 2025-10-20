package org.ageuxo.steelshako.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.heightproviders.BiasedToBottomHeight;
import net.minecraft.world.level.levelgen.placement.*;
import org.ageuxo.steelshako.SteelShakoMod;

import java.util.List;

public class ModPlacements {

    public static final ResourceKey<PlacedFeature> MANGALAN_SPRING = createKey("mangalan_spring_placed");
    public static final ResourceKey<PlacedFeature> GRUELSHROOM_PATCH = createKey("gruelshroom_patch_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        ModPlacements placements = new ModPlacements(context);
        placements.registerPlacements();
    }

    private final BootstrapContext<PlacedFeature> context;
    private final HolderGetter<ConfiguredFeature<?, ?>> lookup;

    private ModPlacements(BootstrapContext<PlacedFeature> context) {
        this.context = context;
        this.lookup = context.lookup(Registries.CONFIGURED_FEATURE);
    }

    public void registerPlacements() {
        register(MANGALAN_SPRING, ModConfiguredFeatures.MANGALAN_SPRING,
                CountPlacement.of(15),
                InSquarePlacement.spread(),
                HeightRangePlacement.of(BiasedToBottomHeight.of(VerticalAnchor.aboveBottom(5), VerticalAnchor.aboveBottom(42), 8))
        );

        register(GRUELSHROOM_PATCH, ModConfiguredFeatures.GRUELSHROOM_PATCH,
                CountPlacement.of(15),
                InSquarePlacement.spread(),
                HeightRangePlacement.of(BiasedToBottomHeight.of(VerticalAnchor.aboveBottom(5), VerticalAnchor.aboveBottom(42), 8))
        );
    }

    protected void register(ResourceKey<PlacedFeature> placement, ResourceKey<ConfiguredFeature<?, ?>> configuredFeature, PlacementModifier... modifiers) {
        this.context.register(placement, new PlacedFeature(lookup.getOrThrow(configuredFeature), List.of(modifiers)));
    }

    protected static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, SteelShakoMod.modRL(name));
    }
}
