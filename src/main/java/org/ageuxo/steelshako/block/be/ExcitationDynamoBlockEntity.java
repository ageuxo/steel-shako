package org.ageuxo.steelshako.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.ageuxo.steelshako.block.multi.ExcitationDynamoPart;
import org.ageuxo.steelshako.block.multi.MultiblockCoreBlockEntity;
import org.ageuxo.steelshako.render.model.ModelProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExcitationDynamoBlockEntity extends MultiblockCoreBlockEntity {

    private final FluidTank waterTank = new FluidTank(16000, f -> f.is(FluidTags.WATER)){
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };
    private final ItemStackHandler fuelStorage = new ItemStackHandler(1){
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getBurnTime(RecipeType.SMELTING) > 0;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private AABB renderBounds;

    public ExcitationDynamoBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.EXCITATION_DYNAMO.get(), pos, blockState);
    }

    @Override
    public @Nullable IItemHandler getItemCap(BlockState state, Direction side) {
        ExcitationDynamoPart part = state.getValue(ExcitationDynamoPart.PROPERTY);
        if (part == ExcitationDynamoPart.FURNACE) {
            return fuelStorage;
        }

        return null;
    }

    @Override
    public @Nullable IFluidHandler getFluidCap(BlockState state, Direction side) {
        ExcitationDynamoPart part = state.getValue(ExcitationDynamoPart.PROPERTY);
        if (part == ExcitationDynamoPart.TANK) {
            return waterTank;
        }

        return null;
    }

    @Override
    public @NotNull ModelData getModelData() {
        return ModelData.builder()
                .with(ModelProperties.OFFSET_PROP, new Vec3i(0, 0, 0))
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
        if (!this.waterTank.isEmpty()){
            tag.put("water", this.waterTank.getFluid().save(registries));
        }
        tag.put("fuel", this.fuelStorage.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.waterTank.setFluid(FluidStack.parseOptional(registries, tag.getCompound("water")));
        this.fuelStorage.deserializeNBT(registries, tag.getCompound("fuel"));
    }

    public AABB renderBounds() {
        if (renderBounds == null) {
            renderBounds = new AABB(this.getBlockPos().above()).inflate(1.5);
        }
        return renderBounds;
    }
}
