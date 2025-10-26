package org.ageuxo.steelshako.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import org.ageuxo.steelshako.ModSounds;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModSoundProvider extends SoundDefinitionsProvider {

    protected final String modId;

    public ModSoundProvider(PackOutput output, String modId, ExistingFileHelper helper) {
        super(output, modId, helper);
        this.modId = modId;
    }

    @Override
    public void registerSounds() {
        addSound(ModSounds.RAY_STARTUP, "ray_startup", "subtitle",
                modSound("ray_startup")
                        .volume(0.5f)
        );
        addSound(ModSounds.RAY_FIRE_START, "ray_fire_start", "subtitle",
                modSound("ray_fire_start")
                        .volume(0.5f)
        );
        addSound(ModSounds.RAY_FIRE_SUSTAIN, "ray_fire_sustain", "subtitle",
                modSound("ray_fire_sustain")
                        .volume(0.5f)
        );
        addSound(ModSounds.RAY_FIRE_END, "ray_fire_end", "subtitle",
                modSound("ray_fire_end")
                        .volume(0.5f)
        );

        addSound(ModSounds.AUTOMATON_HURT, "automaton_hurt", "entity",
                event(SoundEvents.IRON_GOLEM_HURT)
                        .pitch(0.5f)
        );
        addSound(ModSounds.AUTOMATON_DEATH, "automaton_death", "entity",
                event(SoundEvents.IRON_GOLEM_DEATH)
                        .pitch(0.5f)
        );
        addSound(ModSounds.AUTOMATON_IDLE, "automaton_idle", "entity",
                event(SoundEvents.LEVER_CLICK)
                        .volume(0.2)
                        .pitch(2)
        );
    }

    protected void addSound(Supplier<SoundEvent> soundEvent, String name, String prefix, SoundDefinition.Sound... sounds) {
        this.add(soundEvent, SoundDefinition.definition()
                .with(
                        sounds
                )
                .subtitle(
                        prefix +"."+modId+"."+name
                )
        );
    }

    protected static SoundDefinition.@NotNull Sound event(SoundEvent soundEvent) {
        return sound(soundEvent.getLocation(), SoundDefinition.SoundType.EVENT);
    }

    protected SoundDefinition.Sound modSound(String path) {
        return sound(ResourceLocation.fromNamespaceAndPath(modId, path));
    }

}
