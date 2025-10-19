package org.ageuxo.steelshako.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
public class GruelShroomBlock extends Block implements BonemealableBlock {

    public static final MapCodec<GruelShroomBlock> CODEC = simpleCodec(GruelShroomBlock::new);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final Map<Direction, VoxelShape> SHAPES;

    static {
        var shapeMapBuilder = new ImmutableMap.Builder<Direction, VoxelShape>();
        shapeMapBuilder.put(Direction.NORTH, Shapes.box(0.25, 0.375, 0.5625, 0.75, 0.625, 1));
        shapeMapBuilder.put(Direction.EAST, Shapes.box(0, 0.375, 0.25, 0.4375, 0.625, 0.75));
        shapeMapBuilder.put(Direction.SOUTH, Shapes.box(0.25, 0.375, 0.0625, 0.75, 0.625, 0.5));
        shapeMapBuilder.put(Direction.WEST, Shapes.box(0.5625, 0.375, 0.25, 1, 0.625, 0.75));
        SHAPES = shapeMapBuilder.build();
    }

    public GruelShroomBlock(Properties properties) {
        super(properties);
        registerDefaultState(
                defaultBlockState()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(AGE, 0)
        );

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE).add(FACING);
    }

    @Override
    protected @NotNull MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction dir = context.getHorizontalDirection();
        Level level = context.getLevel();
        BlockState state = defaultBlockState()
                .setValue(FACING, dir.getOpposite())
                .setValue(AGE, 0);
        if (canSurvive(state, level, context.getClickedPos())) {
            return state;
        }

        return null;
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.getValue(FACING);
        if (facing == direction && !canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }

        return state;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos attachPos = pos.relative(facing, -1);
        BlockState attached = level.getBlockState(attachPos);
        return SupportType.CENTER.isSupporting(attached, level, attachPos, facing.getOpposite());
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < 3;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(AGE, state.getValue(AGE)+1), Block.UPDATE_ALL);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
