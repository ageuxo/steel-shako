package org.ageuxo.steelshako.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.ageuxo.steelshako.block.multi.ExcitationDynamoPart;
import org.ageuxo.steelshako.block.multi.MultiblockCoreBlockEntity;
import org.ageuxo.steelshako.charge.ChargeHolder;
import org.ageuxo.steelshako.item.component.ModComponents;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExcitationDynamoBlockEntity extends MultiblockCoreBlockEntity implements GeoBlockEntity {

    public static final RawAnimation SPINNING = RawAnimation.begin().thenPlay("spin_up").thenPlay("spinning");
    public static final RawAnimation SPIN_DOWN = RawAnimation.begin().thenPlay("spin_down");
    public static final RawAnimation INSERT_CRYSTAL = RawAnimation.begin().thenPlay("crystal_lift").thenLoop("crystal_bob");

    // Water use per tick
    public static final int WATER_RATE = 5;
    public static final int CHARGE_RATE = 10;

    private AnimatableInstanceCache instanceCache;
    private boolean isBoiling = false;
    private int fuelBurning = 0;
    private int maxFuelBurning = 1;
    private int soundCooldown = 0;

    private final FluidTank waterTank = new FluidTank(16000, f -> f.is(FluidTags.WATER)){
        private Fluid lastFluid = Fluids.WATER;
        @Override
        protected void onContentsChanged() {
            Fluid current = getFluid().getFluid();
            if (lastFluid != current && level != null) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
                    lastFluid = current;
            }
            setChanged();
        }
    };
    private final ItemStackHandler storage = new ItemStackHandler(2){
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0) {
                return stack.getBurnTime(RecipeType.SMELTING) > 0;
            } else {
                return stack.getComponents().has(ModComponents.CHARGE.get());
            }
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private AABB renderBounds;

    public ExcitationDynamoBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.EXCITATION_DYNAMO.get(), pos, blockState);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> isBoiling ? 1 : 0;
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
                        case 0 -> isBoiling = value > 0;
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

    public void chargeItem() {
        // charge inserted chargeable item
        ItemStack stack = storage.getStackInSlot(1);
        if (stack.getItem() instanceof ChargeHolder chargeHolder) {
            chargeHolder.insertCharge(stack, CHARGE_RATE);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ExcitationDynamoBlockEntity be) {
        if (level.isClientSide) {
            if (be.isBoiling()) {
                // sound and animation stuff?
                level.playSound(null, pos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1f, 1f);
            }
            return;
        }

        setChanged(level, pos, state);
        if (be.fuelBurning > 0) { // If there currently is fuel loaded into the boiler
            be.fuelBurning--;
            // Evaporate water up to WATER_RATE
            int evaporated = be.waterTank.drain(WATER_RATE, IFluidHandler.FluidAction.EXECUTE).getAmount();
            if (evaporated >= WATER_RATE) { // If WATER_RATE was evaporated, charge item
                be.chargeItem();
            } else if (evaporated > 0) { // If less than WATER_RATE, but still some water evaporated
                // make some noise to indicate it drying up
                RandomSource random = level.random;
                if (be.soundCooldown <= 0) {
                    level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, random.nextFloat(), random.nextFloat());
                    be.soundCooldown = random.nextInt(10, 40);
                } else {
                    be.soundCooldown--;
                }
            }
        } else if (be.canBoil()) { // If there is fuel in storage and water in the tank
            ItemStack extracted = be.storage.extractItem(0, 1, false);
            int burnTime = extracted.getBurnTime(RecipeType.SMELTING);
            be.fuelBurning = burnTime;
            be.maxFuelBurning = burnTime;
            be.setBoiling(true);
        } else {
            be.setBoiling(false);
        }

    }

    private boolean canBoil() {
        return this.storage.getStackInSlot(0).getCount() > 0 && this.waterTank.getFluidAmount() > 0;
    }

    public boolean isBoiling() {
        return isBoiling;
    }

    public void setBoiling(boolean boiling) {
        isBoiling = boiling;
    }

    @Override
    public @Nullable IItemHandler getItemCap(BlockState state, Direction side) {
        ExcitationDynamoPart part = state.getValue(ExcitationDynamoPart.PROPERTY);
        if (part == ExcitationDynamoPart.FURNACE) {
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
        ExcitationDynamoPart part = state.getValue(ExcitationDynamoPart.PROPERTY);
        if (part == ExcitationDynamoPart.TANK) {
            return waterTank;
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
        tag.put("storage", this.storage.serializeNBT(registries));
        tag.putInt("fuelBurning", this.fuelBurning);
        tag.putInt("maxFuelBurning", this.maxFuelBurning);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.waterTank.setFluid(FluidStack.parseOptional(registries, tag.getCompound("water")));
        this.storage.deserializeNBT(registries, tag.getCompound("storage"));
        this.fuelBurning = tag.getInt("fuelBurning");
        int maxFuelBurning = tag.getInt("maxFuelBurning");
        this.maxFuelBurning = Math.max(maxFuelBurning, 1);
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
            renderBounds = new AABB(this.getBlockPos().above()).inflate(1.5);
        }
        return renderBounds;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "crystal", state -> {
            if (!this.storage.getStackInSlot(1).isEmpty() && this.isBoiling()) {
                return state.setAndContinue(INSERT_CRYSTAL);
            }

            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "coils", state -> {
            if (!this.storage.getStackInSlot(1).isEmpty() && this.isBoiling()) {
                return state.setAndContinue(SPINNING);
            }
            return state.setAndContinue(SPIN_DOWN);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        if (instanceCache == null) {
            instanceCache = GeckoLibUtil.createInstanceCache(this);
        }
        return instanceCache;
    }
}
