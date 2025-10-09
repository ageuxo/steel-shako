package org.ageuxo.steelshako.block.multi;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.ageuxo.steelshako.block.be.DelegatingBlockEntity;
import org.ageuxo.steelshako.block.be.ModBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExcitationDynamoBlock extends Block implements EntityBlock {

    public ExcitationDynamoBlock(Properties properties) {
        super(properties.noOcclusion()
                .isViewBlocking((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ExcitationDynamoPart.PROPERTY).add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        ExcitationDynamoPart part = state.getValue(ExcitationDynamoPart.PROPERTY);
        if (part == ExcitationDynamoPart.CORE){
            return ModBlockEntities.EXCITATION_DYNAMO.get().create(pos, state);
        } else {
            return ModBlockEntities.EXCITATION_PLACEHOLDER.get().create(pos, state);
        }
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        ExcitationDynamoPart part = state.getValue(ExcitationDynamoPart.PROPERTY);
        if (part == ExcitationDynamoPart.FURNACE) {
            // TODO open furnace menu
            return InteractionResult.SUCCESS_NO_ITEM_USED;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var part = state.getValue(ExcitationDynamoPart.PROPERTY);
        if (part == ExcitationDynamoPart.TANK) {
            if (stack.is(Items.BUCKET)) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof DelegatingBlockEntity delegate) {
                    IFluidHandler tank = delegate.getFluidHandler(hitResult.getDirection());
                    if (tank != null && tank.drain(1000, IFluidHandler.FluidAction.SIMULATE).getAmount() == 1000) {
                        tank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                        ItemStack result = ItemUtils.createFilledResult(stack, player, new ItemStack(Items.WATER_BUCKET));
                        player.setItemInHand(hand, result);
                        level.playSound(player, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS);
                        return ItemInteractionResult.CONSUME_PARTIAL;
                    } else {
                        return ItemInteractionResult.FAIL;
                    }
                }
            } else if (stack.is(Items.WATER_BUCKET)) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof DelegatingBlockEntity delegate) {
                    IFluidHandler tank = delegate.getFluidHandler(hitResult.getDirection());
                    FluidStack fluid = new FluidStack(Fluids.WATER, 1000);
                    if (tank != null && tank.fill(fluid, IFluidHandler.FluidAction.SIMULATE) == 1000) {
                        tank.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                        ItemStack result = ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET));
                        player.setItemInHand(hand, result);
                        level.playSound(player, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS);
                        return ItemInteractionResult.CONSUME_PARTIAL;
                    } else {
                        return ItemInteractionResult.FAIL;
                    }
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected @NotNull VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
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
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

}
