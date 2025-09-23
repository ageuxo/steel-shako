package org.ageuxo.steelshako.attachment;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import org.ageuxo.steelshako.network.BlockHeatUpdatePayload;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

public class MiningRayCache {

    public static final Logger LOGGER = LogUtils.getLogger();
    private final Map<BlockPos, Integer> positions = new HashMap<>(); // TODO remove blocks from map when they are updated

    public MiningRayCache(IAttachmentHolder holder) {
    }

    public void tickCooling(ServerLevel level, LevelChunk chunk) {
        Iterator<Map.Entry<BlockPos, Integer>> it = this.positions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Integer> entry = it.next();
            if (entry.getValue() - 1 <= 0) {
                it.remove();
                PacketDistributor.sendToPlayersTrackingChunk(level, chunk.getPos(), new BlockHeatUpdatePayload(entry.getKey(), 0));
            } else {
                PacketDistributor.sendToPlayersTrackingChunk(level, chunk.getPos(), new BlockHeatUpdatePayload(entry.getKey(), entry.getValue() - 1));
                this.positions.put(entry.getKey(), entry.getValue() - 1);
            }
        }

    }

    public void addProgress(Level level, LivingEntity shooter, ItemStack stack, BlockPos pos) {
        int rayProgress = this.positions.getOrDefault(pos, 0);
        float destroySpeed = level.getBlockState(pos).getDestroySpeed(level, pos);
        rayProgress += 8;
        if (rayProgress >= 150) {
            if (!level.isClientSide){
                destroyBlock(level, pos, true, shooter, stack, 512);
                PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunk(pos).getPos(), new BlockHeatUpdatePayload(pos, 0));
            }
            this.positions.remove(pos);
        } else {
            this.positions.put(pos, rayProgress);
            if (!level.isClientSide) {
                PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunk(pos).getPos(), new BlockHeatUpdatePayload(pos, rayProgress));
            }

        }
    }

    public void updateFromServer(BlockPos pos, int heat) {
        if (heat <= 0) {
            this.positions.remove(pos);
        } else {
            this.positions.put(pos, heat);
        }
    }

    // Stack sensitive version of Level#destroyBlock
    public boolean destroyBlock(Level level, BlockPos pos, boolean dropBlock, @Nullable Entity entity, ItemStack stack, int recursionLeft) {
        BlockState blockstate = level.getBlockState(pos);
        if (blockstate.isAir()) {
            return false;
        } else {
            FluidState fluidstate = level.getFluidState(pos);
            if (!(blockstate.getBlock() instanceof BaseFireBlock)) {
                level.levelEvent(2001, pos, Block.getId(blockstate));
            }

            if (dropBlock) {
                BlockEntity blockentity = blockstate.hasBlockEntity() ? level.getBlockEntity(pos) : null;
                Block.dropResources(blockstate, level, pos, blockentity, entity, stack);
            }

            boolean flag = level.setBlock(pos, fluidstate.createLegacyBlock(), 3, recursionLeft);
            if (flag) {
                level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(entity, blockstate));
            }

            return flag;
        }
    }

    public void clearHeat(BlockPos pos) {
        this.positions.remove(pos);
    }

    public int blockHeat(BlockPos pos) {
        Integer boxed = this.positions.get(pos);
        return boxed != null ? boxed : 0;
    }

    public Set<Map.Entry<BlockPos, Integer>> getEntrySet() {
        return this.positions.entrySet();
    }

    public boolean isEmpty() {
        return this.positions.isEmpty();
    }

}
