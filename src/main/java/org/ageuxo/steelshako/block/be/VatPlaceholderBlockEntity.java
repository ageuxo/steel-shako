package org.ageuxo.steelshako.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.ageuxo.steelshako.block.multi.MultiblockDelegate;
import org.ageuxo.steelshako.render.model.ModelProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class VatPlaceholderBlockEntity extends BlockEntity implements MultiblockDelegate {

    private final Vector3i coreOffset = new Vector3i();
    private String model = "";

    public VatPlaceholderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.VAT_PLACEHOLDER.get(), pos, blockState);
    }

    @Nullable
    public IItemHandler getItemHandler(Direction side) {
        //noinspection DataFlowIssue
        BlockEntity blockEntity = this.level.getBlockEntity(this.getBlockPos().offset(this.coreOffset.x, this.coreOffset.y, this.coreOffset.z));
        if (blockEntity instanceof VatBlockEntity vat) {
            return vat.getItemCap(this.getBlockState(), side);
        }
        return null;
    }

    public @Nullable IFluidHandler getFluidHandler(Direction side) {
        //noinspection DataFlowIssue
        BlockEntity blockEntity = this.level.getBlockEntity(this.getBlockPos().offset(this.coreOffset.x, this.coreOffset.y, this.coreOffset.z));
        if (blockEntity instanceof VatBlockEntity vat) {
            return vat.getFluidCap(this.getBlockState(), side);
        }
        return null;
    }

    @Override
    public @NotNull ModelData getModelData() {
        return ModelData.builder()
                .with(ModelProperties.OFFSET_PROP, new Vec3i(coreOffset.x, coreOffset.y, coreOffset.z))
                .build();
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putIntArray("core_offset", new int[]{coreOffset.x, coreOffset.y, coreOffset.z});
        tag.putString("model", this.model);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        coreOffset.set(tag.getIntArray("core_offset"));
        this.model = tag.getString("model");
        requestModelDataUpdate();
    }

    @Override
    public void initDelegate(BlockPos corePos) {
        BlockPos pos = this.getBlockPos();
        this.coreOffset.set(pos.getX() - corePos.getX(), pos.getY() - corePos.getY(), pos.getZ() - corePos.getZ());
        requestModelDataUpdate();
    }
}
