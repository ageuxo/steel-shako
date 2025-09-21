package org.ageuxo.steelshako.attachment;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import javax.annotation.Nullable;
import java.util.*;

public class MiningRayCache {

    private final Map<BlockPos, Integer> positions = new HashMap<>(); // TODO remove blocks from map when they are updated

    public MiningRayCache(IAttachmentHolder holder) {
    }

    public void tickCooling() {
        Iterator<Map.Entry<BlockPos, Integer>> it = this.positions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Integer> entry = it.next();
            if (entry.getValue() - 1 <= 0) {
                it.remove();
            } else {
                this.positions.put(entry.getKey(), entry.getValue() - 1);
            }
        }

    }

    public void addProgress(Level level, LivingEntity shooter, ItemStack stack, BlockPos pos) {
        int rayProgress = this.positions.getOrDefault(pos, 0);
        float destroySpeed = level.getBlockState(pos).getDestroySpeed(level, pos);
        if (++rayProgress >= destroySpeed) {
            destroyBlock(level, pos, true, shooter, stack, 512);
            this.positions.remove(pos);
        } else {
            this.positions.put(pos, rayProgress);
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

    public boolean isEmpty() {
        return this.positions.isEmpty();
    }

}
