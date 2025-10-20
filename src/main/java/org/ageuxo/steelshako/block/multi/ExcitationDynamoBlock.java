package org.ageuxo.steelshako.block.multi;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
import org.ageuxo.steelshako.block.be.ExcitationDynamoBlockEntity;
import org.ageuxo.steelshako.block.be.ModBlockEntities;
import org.ageuxo.steelshako.menu.BoilerMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExcitationDynamoBlock extends BaseMultiBlockBlock {

    public ExcitationDynamoBlock(Properties properties) {
        super(properties.noOcclusion()
                .isViewBlocking((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false)
        );
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (state.getValue(ExcitationDynamoPart.PROPERTY) == ExcitationDynamoPart.CORE) {
            return (level1, pos, state1, be) -> ExcitationDynamoBlockEntity.tick(level1, pos, state1, (ExcitationDynamoBlockEntity) be);
        }

        return null;
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

    @Nullable
    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        if (state.getValue(ExcitationDynamoPart.PROPERTY) == ExcitationDynamoPart.FURNACE) {
            return new SimpleMenuProvider((containerId, playerInventory, player) -> {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof MultiblockCoreBlockEntity core){
                    return new BoilerMenu(containerId, playerInventory, core);
                }
                return null;
            }, Component.translatable("gui.steel_shako.boiler"));
        }
        return null;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        ExcitationDynamoPart part = state.getValue(ExcitationDynamoPart.PROPERTY);
        if (part == ExcitationDynamoPart.FURNACE) {
            ExcitationDynamoBlockEntity core = getCore(level, pos);
            if (!level.isClientSide && core != null && player instanceof ServerPlayer serverPlayer) {
                BlockPos corePos = core.getBlockPos();
                serverPlayer.openMenu(state.getMenuProvider(level, corePos), buf -> buf.writeBlockPos(corePos));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else if (part == ExcitationDynamoPart.CHARGER) {
            ExcitationDynamoBlockEntity core = getCore(level, pos);
            if (!level.isClientSide && core != null) {
                ItemStack chargeSlot = core.getChargeSlot();
                if (chargeSlot.isEmpty()) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, insertChargeItem(player, core)); // Insert item in hand
                    return InteractionResult.PASS;
                } else {
                    ItemStack extracted = core.getItemCapDirect().extractItem(1, 1, false);
                    if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, extracted); // Put in hand if possible
                    } else {
                        player.spawnAtLocation(extracted); // Put itemEntity at player feet
                    }
                    return InteractionResult.SUCCESS_NO_ITEM_USED;
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    private static @NotNull ItemStack insertChargeItem(Player player, ExcitationDynamoBlockEntity core) {
        return core.getItemCapDirect().insertItem(1, player.getItemInHand(InteractionHand.MAIN_HAND), false);
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
        } else if (part == ExcitationDynamoPart.CHARGER || part == ExcitationDynamoPart.CORE) {
            ExcitationDynamoBlockEntity core = getCore(level, pos);
            if (!level.isClientSide && core != null) {
                ItemStack chargeSlot = core.getChargeSlot();
                if (chargeSlot.isEmpty()) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, insertChargeItem(player, core)); // Insert item in hand
                    return ItemInteractionResult.SUCCESS;
                } else {
                    ItemStack extracted = core.getItemCapDirect().extractItem(1, 1, false);
                    player.spawnAtLocation(extracted); // Put itemEntity at player feet
                    return ItemInteractionResult.CONSUME;
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
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

    public @Nullable ExcitationDynamoBlockEntity getCore(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ExcitationDynamoBlockEntity core) {
            return core;
        } else if (blockEntity instanceof DelegatingBlockEntity delegate) {
            Vector3i offset = delegate.getCoreOffset();
            return (ExcitationDynamoBlockEntity) level.getBlockEntity(delegate.getBlockPos().offset(offset.x, offset.y, offset.z));
        }
        return null;
    }

}
