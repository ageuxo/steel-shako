package org.ageuxo.steelshako.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class VatPlaceholderBlockEntity extends BlockEntity {

    private final Vector3i coreOffset = new Vector3i();

    public VatPlaceholderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.VAT_PLACEHOLDER.get(), pos, blockState);
    }

    @Nullable
    public IItemHandler getItemHandler(Direction side) {
        //noinspection DataFlowIssue
        BlockEntity blockEntity = this.level.getBlockEntity(this.getBlockPos().offset(this.coreOffset.x, this.coreOffset.y, this.coreOffset.z));
        if (blockEntity instanceof VatBlockEntity vat) {
            return vat.fuelStorage();
        }
        return null;
    }

    public @Nullable IFluidHandler getFluidHandler(Direction side) {
        //noinspection DataFlowIssue
        BlockEntity blockEntity = this.level.getBlockEntity(this.getBlockPos().offset(this.coreOffset.x, this.coreOffset.y, this.coreOffset.z));
        if (blockEntity instanceof VatBlockEntity vat) {
            return vat.waterTank();
        }
        return null;
    }

    @Override
    public @NotNull ModelData getModelData() {
        return super.getModelData(); // TODO is this what should determine placeholder models?
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putIntArray("core_offset", new int[]{coreOffset.x, coreOffset.y, coreOffset.z});
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        coreOffset.set(tag.getIntArray("core_offset"));
    }
}
