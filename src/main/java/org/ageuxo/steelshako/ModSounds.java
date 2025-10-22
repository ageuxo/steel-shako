package org.ageuxo.steelshako;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT,  SteelShakoMod.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> RAY_STARTUP = register("ray_startup");
    public static final DeferredHolder<SoundEvent, SoundEvent> RAY_FIRE_START = register("ray_fire_start");
    public static final DeferredHolder<SoundEvent, SoundEvent> RAY_FIRE_SUSTAIN = register("ray_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> RAY_FIRE_END = register("ray_fire_end");

    private static @NotNull DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(SteelShakoMod.modRL(name)));
    }

}
