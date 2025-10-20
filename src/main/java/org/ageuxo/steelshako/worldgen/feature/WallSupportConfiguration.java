package org.ageuxo.steelshako.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

import java.util.Objects;

public record WallSupportConfiguration(int horizontalRadius, int verticalRadius, ResourceLocation blockId, RuleTest predicate) implements FeatureConfiguration {
    public WallSupportConfiguration(int horizontalRadius, int verticalRadius, Holder<Block> block, RuleTest predicate) {
        this(horizontalRadius, verticalRadius, Objects.requireNonNull(block.getKey()).location(), predicate);
    }

    public static final Codec<WallSupportConfiguration> CODEC = RecordCodecBuilder.create(instance->instance.group(
            Codec.INT.fieldOf("horizontal_radius").forGetter(WallSupportConfiguration::horizontalRadius),
            Codec.INT.fieldOf("vertical_radius").forGetter(WallSupportConfiguration::verticalRadius),
            ResourceLocation.CODEC.fieldOf("block_id").forGetter(WallSupportConfiguration::blockId),
            RuleTest.CODEC.fieldOf("predicate").forGetter(WallSupportConfiguration::predicate)
    ).apply(instance, WallSupportConfiguration::new));


}
