package org.ageuxo.steelshako.block.multi;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Vector3i;

public interface MultiblockDelegate {
    void initDelegate(BlockPos corePos);
    Vector3i getCoreOffset();
    boolean isDisassembled();
    void setDisassembled(boolean bool);
    BlockPos getBlockPos();

    default void disassemble(Level level, BlockPos sourcePos) {
        if (isDisassembled()) {
            LogUtils.getLogger().error("Tried to disassemble MultiblockDelegate at {} twice!", sourcePos);
            return;
        }
        setDisassembled(true);
        Vector3i offset = getCoreOffset();
        BlockEntity blockEntity = level.getBlockEntity(getBlockPos().offset(offset.x, offset.y, offset.z));
        if (blockEntity instanceof MultiblockCore core) {
            core.disassemble();
        }
    }
}
