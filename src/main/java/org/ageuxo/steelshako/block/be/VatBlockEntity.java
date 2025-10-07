package org.ageuxo.steelshako.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.ageuxo.steelshako.block.multi.MultiblockCore;
import org.ageuxo.steelshako.block.multi.MultiblockDelegate;
import org.ageuxo.steelshako.block.multi.VatPart;
import org.ageuxo.steelshako.render.model.ModelProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class VatBlockEntity extends BlockEntity implements MultiblockCore {

    private final FluidTank waterTank = new VatTank(16000, f -> f.is(FluidTags.WATER));
    private final FluidTank slopTank = new VatTank(16000);
    private final ItemStackHandler fuelStorage = new ItemStackHandler(1) {
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

    public VatBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.GRUEL_VAT.get(), pos, blockState);
    }

    public ItemStackHandler fuelStorage() {
        return fuelStorage;
    }

    public FluidTank waterTank() {
        return waterTank;
    }

    public FluidTank slopTank() {
        return slopTank;
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
    public void initCore(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (BlockPos pos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
            if (level != null && level.getBlockEntity(pos) instanceof MultiblockDelegate delegate) {
                delegate.initDelegate(getBlockPos());
            }
        }

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
        if (!this.slopTank.isEmpty()){
            tag.put("slop", this.slopTank.getFluid().save(registries));
        }
        tag.put("fuel", this.fuelStorage.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.waterTank.setFluid(FluidStack.parseOptional(registries, tag.getCompound("water")));
        this.slopTank.setFluid(FluidStack.parseOptional(registries, tag.getCompound("slop")));
        this.fuelStorage.deserializeNBT(registries, tag.getCompound("fuel"));
    }

    public AABB renderBounds() {
        if (renderBounds == null) {
            renderBounds = AABB.ofSize(this.getBlockPos().getCenter().add(0, 1, 0), 5, 4, 5);
        }
        return renderBounds;
    }

    public class VatTank extends FluidTank {
        public VatTank(int capacity) {
            super(capacity);
        }

        public VatTank(int capacity, Predicate<FluidStack> validator) {
            super(capacity, validator);
        }

        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    }

}
