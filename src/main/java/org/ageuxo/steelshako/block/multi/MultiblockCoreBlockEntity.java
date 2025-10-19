package org.ageuxo.steelshako.block.multi;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3i;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class MultiblockCoreBlockEntity extends BlockEntity implements MultiblockCore {

    protected boolean isDisassembled;
    protected Vector3i minCorner = new Vector3i();
    protected Vector3i maxCorner = new Vector3i();
    protected ContainerData data;

    public MultiblockCoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void initCore(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minCorner = new Vector3i(minX, minY, minZ);
        this.maxCorner = new Vector3i(maxX, maxY, maxZ);
        initDelegates(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public boolean isDisassembled() {
        return isDisassembled;
    }

    public void setDisassembled(boolean disassembled) {
        isDisassembled = disassembled;
    }

    @Override
    public void disassemble() {
        Vector3i min = minCorner();
        Vector3i max = maxCorner();
        for (BlockPos pos : BlockPos.betweenClosed(min.x, min.y, min.z, max.x, max.y, max.z)) {
            //noinspection DataFlowIssue
            if (level.getBlockEntity(pos) instanceof MultiblockDelegate delegate) {
                delegate.setDisassembled(true);
                level.removeBlock(pos, false);
            }
        }
        dropContents();
        //noinspection DataFlowIssue
        level.destroyBlock(getBlockPos(), true);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        Vector3i min = this.minCorner;
        tag.putIntArray("corner_min", new int[]{min.x, min.y, min.z});
        Vector3i max = this.maxCorner;
        tag.putIntArray("corner_max", new int[]{max.x, max.y, max.z});
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        int[] min = tag.getIntArray("corner_min");
        this.minCorner.set(min.length == 3 ? min : new int[]{0, 0, 0});
        int[] max = tag.getIntArray("corner_max");
        this.minCorner.set(max.length == 3 ? max : new int[]{0, 0, 0});
    }

    public abstract void dropContents();

    public Vector3i minCorner() {
        return minCorner;
    }

    public Vector3i maxCorner() {
        return maxCorner;
    }

    public ContainerData data() {
        return data;
    }
}
