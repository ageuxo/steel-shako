package org.ageuxo.steelshako.render.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RayParticle extends TextureSheetParticle {

    protected final double toX;
    protected final double toY;
    protected final double toZ;
    protected Vec3 to;
    protected final SpriteSet spriteSet;

    public RayParticle(ClientLevel level, double fromX, double fromY, double fromZ, double toX, double toY, double toZ, SpriteSet spriteSet) {
        super(level, fromX, fromY, fromZ);
        this.toX = toX;
        this.toY = toY;
        this.toZ = toZ;
        this.spriteSet = spriteSet;
        this.gravity = 0;
        this.hasPhysics = false;
        this.lifetime = 100;

        this.setSpriteFromAge(spriteSet);
        this.to = new Vec3(toX, toY, toZ);
        this.setBoundingBox(new AABB(fromX, fromY, fromZ, toX, toY, toZ).inflate(0.4));
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 15728880; // LightTexture.FULL_BRIGHT, inlined to avoid classloading
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        int packedLight = getLightColor(partialTicks);
        float beamWidth = 0.1f;
        Vector3f camPos = renderInfo.getPosition().toVector3f();
        Vector3f from = new Vector3f((float) x, (float) y, (float) z).sub(camPos);
        Vector3f to = new Vector3f((float) toX, (float) toY, (float) toZ).sub(camPos);
        addBoxBetween(buffer, from, to, beamWidth, packedLight);

    }

    public void addBoxBetween(VertexConsumer buffer, Vector3f p1, Vector3f p2, float thickness, int packedLight) {
        Matrix4f transform = makeBoxTransform(p1, p2, thickness);

        // Front (+Z)
        addQuad(buffer, transform, -1, -1, 1,
                1, -1, 1,
                1, 1, 1,
                -1, 1, 1,
                packedLight);

        // Back (-Z)
        addQuad(buffer, transform, 1, -1, -1,
                -1, -1, -1,
                -1, 1, -1,
                1, 1, -1,
                packedLight);

        // Left (-X)
        addQuad(buffer, transform, -1, -1, -1,
                -1, -1, 1,
                -1, 1, 1,
                -1, 1, -1,
                packedLight);

        // Right (+X)
        addQuad(buffer, transform, 1, -1, 1,
                1, -1, -1,
                1, 1, -1,
                1, 1, 1,
                packedLight);

        // Top (+Y)
        addQuad(buffer, transform,
                1, 1, 1,
                1, 1, -1,
                -1, 1, -1,
                -1, 1, 1,
                packedLight);

        // Bottom (-Y)
        addQuad(buffer, transform,
                1, -1, 1,
                1, -1, -1,
                -1, -1, 1,
                -1, -1, -1,
                packedLight);
    }

    private Matrix4f makeBoxTransform(Vector3f p1, Vector3f p2, float thickness) {
        Vector3f center = new Vector3f(p1).add(p2).mul(0.5f);

        Vector3f axis = new Vector3f(p2).sub(p1);
        float length = axis.length();
        axis.normalize();

        Quaternionf rotation = new Quaternionf()
                .rotateTo(new Vector3f(0, 0, 1), axis);

        return new Matrix4f()
                .translate(center)
                .rotate(rotation)
                .scale(thickness, thickness, length * 0.5f);
    }

    private void addQuad(VertexConsumer buffer, Matrix4f transform,
                         float x1, float y1, float z1,
                         float x2, float y2, float z2,
                         float x3, float y3, float z3,
                         float x4, float y4, float z4, int packedLight) {
        addVertex(buffer, transform, x1, y1, z1, getU0(), getV0(), packedLight);
        addVertex(buffer, transform, x2, y2, z2, getU1(), getV0(), packedLight);
        addVertex(buffer, transform, x3, y3, z3, getU1(), getV1(), packedLight);
        addVertex(buffer, transform, x4, y4, z4, getU0(), getV1(), packedLight);
    }

    private void addVertex(VertexConsumer buffer, Matrix4f transform, float x, float y, float z, float u, float v, int packedLight) {
        Vector4f pos = new Vector4f(x, y, z, 1f).mul(transform);
        buffer.addVertex(pos.x(), pos.y(), pos.z())
                .setUv(u, v)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha * (1 - easeOutExpo((float) this.age / this.lifetime)))
                .setLight(packedLight);
    }

    public static float easeOutExpo(float x) {
        return x == 1 ? 1 : (float) (1 - Math.pow(2, -10 * x));
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(float partialTicks) {
        return getBoundingBox();
    }
}
