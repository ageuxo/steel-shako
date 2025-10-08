package org.ageuxo.steelshako.block.multi;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public interface MultiblockCore {
    Level getLevel();
    BlockPos getBlockPos();

    default void initCore(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (BlockPos pos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
            if (getLevel() != null && getLevel().getBlockEntity(pos) instanceof MultiblockDelegate delegate) {
                delegate.initDelegate(getBlockPos());
            }
        }

    }

    @Nullable
    IItemHandler getItemCap(BlockState state, Direction side);

    @Nullable
    IFluidHandler getFluidCap(BlockState state, Direction side);
}
