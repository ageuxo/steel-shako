package org.ageuxo.steelshako.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.ageuxo.steelshako.SteelShakoMod;

public class ModFluids {

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, SteelShakoMod.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> GRUEL_TYPE = FLUID_TYPES.register("gruel",
            ()-> new FluidType(FluidType.Properties.create()
                    .fallDistanceModifier(0.2f)
                    .supportsBoating(true)
                    .canExtinguish(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                    .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
            )
    );

    public static final DeferredHolder<Fluid, BaseFlowingFluid> GRUEL = DeferredHolder.create(Registries.FLUID, SteelShakoMod.modRL("gruel"));
    public static final DeferredHolder<Fluid, BaseFlowingFluid> GRUEL_FLOWING = DeferredHolder.create(Registries.FLUID, SteelShakoMod.modRL("gruel_flowing"));

    public static void registerFluids(RegisterEvent event) {

        event.register(Registries.FLUID, helper -> {
            BaseFlowingFluid.Properties gruel_props = new BaseFlowingFluid.Properties(GRUEL_TYPE, GRUEL, GRUEL_FLOWING)
                    .block(ModBlocks.GRUEL_FLUID);

            helper.register(GRUEL.getId(), new BaseFlowingFluid.Source(gruel_props));
            helper.register(GRUEL_FLOWING.getId(), new BaseFlowingFluid.Flowing(gruel_props));
        });
    }


}
