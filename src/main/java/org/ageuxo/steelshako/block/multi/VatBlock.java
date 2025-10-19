package org.ageuxo.steelshako.block.multi;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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
import org.ageuxo.steelshako.block.be.ModBlockEntities;
import org.ageuxo.steelshako.block.be.VatBlockEntity;
import org.ageuxo.steelshako.block.be.DelegatingBlockEntity;
import org.ageuxo.steelshako.menu.BoilerMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class VatBlock extends BaseMultiBlockBlock {

    public VatBlock(Properties properties) {
        super(properties.noOcclusion()
                .isViewBlocking((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false)
        );
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (state.getValue(VatPart.PROPERTY) == VatPart.CORE) {
            return (level1, pos, state1, be) -> VatBlockEntity.tick(level1, pos, state1, (VatBlockEntity) be);
        }

        return null;
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
            return ModBlockEntities.VAT_PLACEHOLDER.get().create(pos, state);
        }
    }

    @Nullable
    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        if (state.getValue(VatPart.PROPERTY) == VatPart.FURNACE) {
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
        VatPart part = state.getValue(VatPart.PROPERTY);
        if (part == VatPart.FURNACE) {
            VatBlockEntity core = getCore(level, pos);
            if (!level.isClientSide && core != null && player instanceof ServerPlayer serverPlayer) {
                BlockPos corePos = core.getBlockPos();
                serverPlayer.openMenu(state.getMenuProvider(level, corePos), buf -> buf.writeBlockPos(corePos));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        VatPart part = state.getValue(VatPart.PROPERTY);
        if (part == VatPart.TANK)
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
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(VatPart.PROPERTY) == VatPart.CORE){
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            BlockEntity blockEntity = level.getBlockEntity(pos);
            Direction rot90 = facing.getCounterClockWise();
            BlockPos furnacePos =
                    pos.relative(rot90, 2)
                            .relative(facing, 1);
            BlockPos chimney = furnacePos.offset(0, 2, 0);
            if (blockEntity instanceof VatBlockEntity vat) {
                if (vat.data().get(1) > 0) {
                    if (random.nextInt(10) == 0) {
                        level.playLocalSound(
                                (double) furnacePos.getX() + 0.5,
                                (double) furnacePos.getY() + 0.5,
                                (double) furnacePos.getZ() + 0.5,
                                SoundEvents.CAMPFIRE_CRACKLE,
                                SoundSource.BLOCKS,
                                0.5F + random.nextFloat(),
                                random.nextFloat() * 0.7F + 0.6F,
                                false
                        );
                    }

                    if (random.nextInt(3) == 0) {
                        for (int i = 0; i < random.nextInt(1) + 1; i++) {
                            level.addParticle(
                                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                    (double)chimney.getX() + 0.5 + random.nextDouble() / 6.0 * (double)(random.nextBoolean() ? 1 : -1),
                                    (double)chimney.getY() + 1.2,
                                    (double)chimney.getZ() + 0.5 + random.nextDouble() / 6.0 * (double)(random.nextBoolean() ? 1 : -1),
                                    0.001 * random.nextFloat(),
                                    0.07,
                                    0.001 * random.nextFloat()
                            );
                        }
                    }
                }
            }
        }
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

    public @Nullable VatBlockEntity getCore(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof VatBlockEntity core) {
            return core;
        } else if (blockEntity instanceof DelegatingBlockEntity delegate) {
            Vector3i offset = delegate.getCoreOffset();
            return (VatBlockEntity) level.getBlockEntity(delegate.getBlockPos().offset(offset.x, offset.y, offset.z));
        }
        return null;
    }


}
