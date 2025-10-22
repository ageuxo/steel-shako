package org.ageuxo.steelshako.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import org.ageuxo.steelshako.ModSounds;

import java.util.function.Supplier;

public class ModSoundProvider extends SoundDefinitionsProvider {

    protected final String modId;

    public ModSoundProvider(PackOutput output, String modId, ExistingFileHelper helper) {
        super(output, modId, helper);
        this.modId = modId;
    }

    @Override
    public void registerSounds() {
        addSound(ModSounds.RAY_STARTUP, "ray_startup");
        addSound(ModSounds.RAY_FIRE_START, "ray_fire_start");
        addSound(ModSounds.RAY_FIRE_SUSTAIN, "ray_fire_sustain");
        addSound(ModSounds.RAY_FIRE_END, "ray_fire_end");
    }

    private void addSound(Supplier<SoundEvent> soundEvent, String name) {
        this.add(soundEvent, SoundDefinition.definition()
                .with(
                        addSound(name)
                                .volume(0.5f)
                                .pitch(1)
                                .weight(1)
                )
                .subtitle("subtitle."+modId+"."+name)
        );
    }

    protected SoundDefinition.Sound addSound(String path) {
        return sound(ResourceLocation.fromNamespaceAndPath(modId, path));
    }

}
