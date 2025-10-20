package org.ageuxo.steelshako.worldgen.feature;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class WallSupportFeature extends Feature<WallSupportConfiguration> {

    public WallSupportFeature() {
        super(WallSupportConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<WallSupportConfiguration> context) {
        WallSupportConfiguration config = context.config();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        int placed = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos adjacent = new BlockPos.MutableBlockPos();
        int horiz = config.horizontalRadius();
        int vert = config.verticalRadius();
        ResourceLocation blockId = config.blockId();
        Block block = level.registryAccess().registryOrThrow(Registries.BLOCK).get(blockId);
        if (block == null) {
            throw new IllegalStateException("Missing block in WallSupportFeature: %s".formatted(blockId));
        }

        if (config.predicate().test(level.getBlockState(origin), random)){
            for (int i = 0; i < 64; i++) {
                pos.setWithOffset(
                        origin,
                        random.nextInt(horiz) - random.nextInt(horiz),
                        random.nextInt(vert) - random.nextInt(vert),
                        random.nextInt(horiz) - random.nextInt(horiz)
                );
                BlockState state = level.getBlockState(pos);
                if (!state.isAir()) {
                    for (Direction face : Direction.Plane.HORIZONTAL) {
                        if (SupportType.CENTER.isSupporting(state, level, pos, face)) {
                            adjacent.setWithOffset(pos, face);
                            BlockState adjacentState = level.getBlockState(adjacent);
                            if (adjacentState.isAir() || adjacentState.canBeReplaced()) {
                                if (level.setBlock(adjacent, block.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, face), Block.UPDATE_NONE)) {
                                    placed++;
                                }
                            }
                        }
                    }
                }
            }
        }

        return placed > 0;
    }

}
