package org.ageuxo.steelshako.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.ageuxo.steelshako.block.multi.MultiblockCore;
import org.ageuxo.steelshako.block.multi.MultiblockDelegate;
import org.ageuxo.steelshako.render.model.ModelProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DelegatingBlockEntity extends BlockEntity implements MultiblockDelegate {

    private final Vector3i modelPos = new Vector3i();
    private final Vector3i coreOffset = new Vector3i();

    private DelegatingBlockEntity(BlockEntityType<DelegatingBlockEntity> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public static DelegatingBlockEntity gruelVat(BlockPos pos, BlockState state) {
        return new DelegatingBlockEntity(ModBlockEntities.VAT_PLACEHOLDER.get(), pos, state);
    }

    public static DelegatingBlockEntity excitationDynamo(BlockPos pos, BlockState state) {
        return new DelegatingBlockEntity(ModBlockEntities.EXCITATION_PLACEHOLDER.get(), pos, state);
    }

    @Nullable
    public IItemHandler getItemHandler(Direction side) {
        //noinspection DataFlowIssue
        BlockEntity blockEntity = this.level.getBlockEntity(this.getBlockPos().offset(this.coreOffset.x, this.coreOffset.y, this.coreOffset.z));
        if (blockEntity instanceof MultiblockCore core) {
            return core.getItemCap(this.getBlockState(), side);
        }
        return null;
    }

    public @Nullable IFluidHandler getFluidHandler(Direction side) {
        //noinspection DataFlowIssue
        BlockEntity blockEntity = this.level.getBlockEntity(this.getBlockPos().offset(this.coreOffset.x, this.coreOffset.y, this.coreOffset.z));
        if (blockEntity instanceof MultiblockCore core) {
            return core.getFluidCap(this.getBlockState(), side);
        }
        return null;
    }

    @Override
    public @NotNull ModelData getModelData() {
        return ModelData.builder()
                .with(ModelProperties.OFFSET_PROP, new Vec3i(modelPos.x, modelPos.y, modelPos.z))
                .build();
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putIntArray("model_offset", new int[]{modelPos.x, modelPos.y, modelPos.z});
        tag.putIntArray("core_offset", new int[]{coreOffset.x, coreOffset.y, coreOffset.z});
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        int[] modelOffset = tag.getIntArray("model_offset");
        this.modelPos.set(modelOffset.length == 3 ? modelOffset : new int[]{0, 0, 0});
        int[] coreOffset = tag.getIntArray("core_offset");
        this.coreOffset.set(coreOffset.length == 3 ? coreOffset : new int[]{0, 0, 0});
        requestModelDataUpdate();
    }

    @Override
    public void initDelegate(BlockPos corePos) {
        BlockPos pos = this.getBlockPos();
        this.coreOffset.set(corePos.getX() - pos.getX(), corePos.getY() - pos.getY(), corePos.getZ() - pos.getZ());
    }
}
