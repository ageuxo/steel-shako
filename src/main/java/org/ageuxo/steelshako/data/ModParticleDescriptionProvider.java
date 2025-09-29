package org.ageuxo.steelshako.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.render.particle.ModParticles;

public class ModParticleDescriptionProvider extends ParticleDescriptionProvider {


    public ModParticleDescriptionProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper);
    }

    @Override
    protected void addDescriptions() {
        sprite(ModParticles.MINING_RAY_BEAM.get(), SteelShakoMod.modRL("mining_ray_beam"));


    }
}
