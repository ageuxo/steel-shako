package org.ageuxo.steelshako.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.worldgen.feature.WallSupportFeature;

public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, SteelShakoMod.MOD_ID);

    public static final DeferredHolder<Feature<?>, WallSupportFeature> WALL_SUPPORT = FEATURES.register("wall_support", WallSupportFeature::new);


}
