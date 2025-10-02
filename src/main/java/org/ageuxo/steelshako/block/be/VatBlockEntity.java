package org.ageuxo.steelshako.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.ageuxo.steelshako.block.multi.VatPart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class VatBlockEntity extends BlockEntity {

    private final FluidTank waterTank = new FluidTank(16000, f -> f.is(FluidTags.WATER));
    private final FluidTank slopTank = new FluidTank(16000);
    private final ItemStackHandler fuelStorage = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getBurnTime(RecipeType.SMELTING) > 0;
        }
    };

    private Direction facing;

    public VatBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.GRUEL_VAT.get(), pos, blockState);
    }

    public ItemStackHandler fuelStorage() {
        return fuelStorage;
    }

    public FluidTank waterTank() {
        return waterTank;
    }

    public @Nullable IItemHandler getItemCap(BlockState state, Direction side) {
        VatPart part = state.getValue(VatPart.PROPERTY);
        if (part == VatPart.FURNACE) {
            return fuelStorage;
        }

        return null;
    }

    public @Nullable IFluidHandler getFluidCap(BlockState state, Direction side) {
        VatPart part = state.getValue(VatPart.PROPERTY);
        if (part == VatPart.TANK) {
            return waterTank;
        } else if (part == VatPart.VAT) {
            return slopTank;
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
        this.waterTank.writeToNBT(registries, tag);

        tag.put("fuel", this.fuelStorage.serializeNBT(registries));
        tag.putString("facing", this.facing.getSerializedName());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.waterTank.readFromNBT(registries, tag);
        this.fuelStorage.deserializeNBT(registries, tag.getCompound("fuel"));
        Direction facing = Direction.byName(tag.getString("facing"));
        if (facing != null) {
            this.facing = facing;
        }
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

}
