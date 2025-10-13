package org.ageuxo.steelshako.render.particle.provider;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import org.ageuxo.steelshako.render.particle.RayParticle;
import org.ageuxo.steelshako.render.particle.VectorOption;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RayParticleProvider implements ParticleProvider<VectorOption> {

    private final SpriteSet spriteSet;

    public RayParticleProvider(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    @Nullable
    @Override
    public Particle createParticle(VectorOption type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        Vector3f vector = type.vector();
        return new RayParticle(level, x, y, z, vector.x, vector.y, vector.z, this.spriteSet);
    }
}
