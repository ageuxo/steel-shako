package org.ageuxo.steelshako.block.multi;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.ageuxo.steelshako.block.be.VatBlockEntity;
import org.ageuxo.steelshako.block.be.VatPlaceholderBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class VatBlock extends Block implements EntityBlock {

    public VatBlock(Properties properties) {
        super(properties.noOcclusion().isViewBlocking((state, level, pos) -> false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VatPart.PROPERTY).add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        VatPart part = state.getValue(VatPart.PROPERTY);
        if (part == VatPart.CORE) {
            return new VatBlockEntity(pos, state);
        } else {
            return new VatPlaceholderBlockEntity(pos, state);
        }
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        VatPart part = state.getValue(VatPart.PROPERTY);
        if (part == VatPart.FURNACE) {
            // TODO open furnace menu
            level.blockUpdated(pos, state.getBlock());
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof VatPlaceholderBlockEntity vatBlockEntity) {
                vatBlockEntity.requestModelDataUpdate();
            }
            return InteractionResult.SUCCESS_NO_ITEM_USED;
        } else if (part == VatPart.CORE) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof VatBlockEntity vatBlockEntity) {
                vatBlockEntity.requestModelDataUpdate();
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        return state.setValue(BlockStateProperties.HORIZONTAL_FACING, mirror.mirror(facing));
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotate(facing));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }


}
