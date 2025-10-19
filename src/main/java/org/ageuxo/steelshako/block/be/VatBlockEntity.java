package org.ageuxo.steelshako.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.ageuxo.steelshako.block.ModFluids;
import org.ageuxo.steelshako.block.multi.MultiblockCoreBlockEntity;
import org.ageuxo.steelshako.block.multi.VatPart;
import org.ageuxo.steelshako.item.ModItems;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class VatBlockEntity extends MultiblockCoreBlockEntity {

    public static final int WATER_COST = 8000;
    public static final int PROCESS_TIME = 3600;

    private final FluidTank waterTank = new VatTank(16000, f -> f.is(FluidTags.WATER));
    private final FluidTank slopTank = new VatTank(16000);
    private final ItemStackHandler storage = new ItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0) {
                return stack.getBurnTime(RecipeType.SMELTING) > 0;
            } else if (slot == 1) {
                return stack.is(ModItems.GRUEL_SPORES);
            }

            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private AABB renderBounds;
    private BlockPos furnacePos;
    private int progress = 0;
    private int fuelBurning = 0;
    private int maxFuelBurning = 1;
    private int soundCooldown = 0;

    public VatBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.GRUEL_VAT.get(), pos, blockState);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> fuelBurning;
                    case 2 -> maxFuelBurning;
                    case 3 -> waterTank.getFluidAmount();
                    case 4 -> waterTank.getCapacity();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (level != null && level.isClientSide) {
                    switch (index) {
                        case 0 -> progress = value;
                        case 1 -> fuelBurning = value;
                        case 2 -> maxFuelBurning = value;
                    }
                }
            }

            @Override
            public int getCount() {
                return 5;
            }
        };
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VatBlockEntity be) {
        if (level.isClientSide) {
            if (be.isBoiling()) {
                // sound and animation stuff?
                level.playSound(null, pos, SoundEvents.GENERIC_BURN, SoundSource.BLOCKS, 1f, 1f);

            }
            return;
        }

        if (be.soundCooldown > 0) {
            be.soundCooldown--;
        }

        if (level.getGameTime() % 4 == 0 && be.storage.getStackInSlot(1).isEmpty()) {
            AABB vatOpening = new AABB(be.getBlockPos()).inflate(1, 1.2, 1).move(0, 1.5, 0);
            List<ItemEntity> entities = level.getEntities(EntityType.ITEM, vatOpening, EntitySelector.ENTITY_STILL_ALIVE);
            for (ItemEntity e : entities) {
                ItemStack stack = e.getItem();
                if (stack.is(ModItems.GRUEL_SPORES)) {
                    ItemStack remaining = be.storage().insertItem(1, stack, false);
                    e.setItem(remaining);
                    if (be.soundCooldown <= 0) {
                        level.playSound(null, be.getBlockPos(), SoundEvents.PLAYER_BURP, SoundSource.BLOCKS);
                        be.soundCooldown = level.random.nextInt(15, 45);
                    }
                    break;
                }
            }
        }

        setChanged(level, pos, state);
        if (be.fuelBurning > 0) { // If there currently is fuel loaded into the furnace
            be.progress++;
            be.fuelBurning--;
            if (be.progress >= PROCESS_TIME) {
                be.progress = 0;
                be.cookSoup();
            }
        } else if (be.progress > 0 && !be.storage.getStackInSlot(0).isEmpty()) {
            be.burnFuel();
        } else if (be.canBoil() && be.canStartCooking()) { // If there is fuel, water & shroom in inventory
            be.storage.extractItem(1, 64, false);
            be.waterTank.drain(8000, IFluidHandler.FluidAction.EXECUTE);
            be.burnFuel();
        } else {
            be.progress = 0;
        }
        be.syncClientData();

    }

    private void burnFuel() {
        ItemStack extractedFuel = this.storage.extractItem(0, 1, false);
        int burnTime = extractedFuel.getBurnTime(RecipeType.SMELTING);
        this.fuelBurning = burnTime;
        this.maxFuelBurning = burnTime;
    }

    protected void syncClientData() {
        BlockState state = getBlockState();
        //noinspection DataFlowIssue
        level.sendBlockUpdated(getBlockPos(), state, state, Block.UPDATE_ALL);
    }

    private void cookSoup() {
        this.slopTank.fill(new FluidStack(ModFluids.GRUEL, 10), IFluidHandler.FluidAction.EXECUTE);
    }

    private boolean canStartCooking() {
        return this.progress == 0 && !this.storage.getStackInSlot(1).isEmpty() && !(this.slopTank.getFluidAmount() >= this.slopTank.getCapacity());
    }

    private boolean canBoil() {
        return this.storage.getStackInSlot(0).getCount() > 0 && this.waterTank.getFluidAmount() >= WATER_COST;
    }

    public boolean isBoiling() {
        return progress > 0;
    }

    public ItemStackHandler storage() {
        return storage;
    }

    public FluidTank waterTank() {
        return waterTank;
    }

    public FluidTank slopTank() {
        return slopTank;
    }

    public int progress() {
        return progress;
    }

    @Override
    public @Nullable IItemHandler getItemCap(BlockState state, Direction side) {
        VatPart part = state.getValue(VatPart.PROPERTY);
        if (part == VatPart.FURNACE) {
            return storage;
        }

        return null;
    }

    @Override
    public IItemHandler getItemCapDirect() {
        return storage;
    }

    @Override
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
    public IFluidHandler getFluidCapDirect() {
        return waterTank;
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
        tag.put("storage", this.storage.serializeNBT(registries));
        tag.putInt("progress", this.progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.waterTank.setFluid(FluidStack.parseOptional(registries, tag.getCompound("water")));
        this.slopTank.setFluid(FluidStack.parseOptional(registries, tag.getCompound("slop")));
        this.storage.deserializeNBT(registries, tag.getCompound("storage"));
        this.progress = tag.getInt("progress");
    }

    @Override
    public void dropContents() {
        if (level != null){
            for (int i = 0; i < this.storage.getSlots(); i++) {
                ItemStack stack = this.storage.extractItem(i, Integer.MAX_VALUE, false);
                Block.popResource(level, getBlockPos(), stack);
            }
        }
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
