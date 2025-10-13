package org.ageuxo.steelshako;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.ageuxo.steelshako.block.ModFluids;
import org.ageuxo.steelshako.block.be.ModBlockEntities;
import org.ageuxo.steelshako.entity.ModEntityTypes;
import org.ageuxo.steelshako.entity.render.RayRenderer;
import org.ageuxo.steelshako.item.ModItems;
import org.ageuxo.steelshako.render.ArmPoseExtension;
import org.ageuxo.steelshako.render.ItemHandPoses;
import org.ageuxo.steelshako.render.MiningRayProgressRenderer;
import org.ageuxo.steelshako.render.ber.VatBlockEntityRenderer;
import org.ageuxo.steelshako.render.geo.AutomatonRenderer;
import org.ageuxo.steelshako.render.model.MultiblockGeometryLoader;
import org.ageuxo.steelshako.render.particle.ModParticles;
import org.ageuxo.steelshako.render.particle.provider.RayParticleProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(value = Dist.CLIENT, modid = SteelShakoMod.MOD_ID)
public class ClientEvents {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.RAY.get(), RayRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.AUTOMATON.get(), AutomatonRenderer::new);

        event.registerBlockEntityRenderer(ModBlockEntities.GRUEL_VAT.get(), VatBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(MultiblockGeometryLoader.ID, MultiblockGeometryLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        event.register(RayRenderer.MODEL);
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            MiningRayProgressRenderer.render(event.getPoseStack(), event.getCamera());
        }

    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Nullable
            @Override
            public HumanoidModel.ArmPose getArmPose(@NotNull LivingEntity entityLiving, @NotNull InteractionHand hand, @NotNull ItemStack itemStack) {
                return ArmPoseExtension.RAYGUN_ARMPOSE_PROXY.getValue();
            }

            @Override
            public boolean applyForgeHandTransform(@NotNull PoseStack poseStack, @NotNull LocalPlayer player, @NotNull HumanoidArm arm, @NotNull ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
                ItemHandPoses.applyItemArmTransform(poseStack, arm, equipProcess);
                return true;
            }
        }, ModItems.MINING_RAY_GUN.get());

        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public @NotNull ResourceLocation getStillTexture() {
                return SteelShakoMod.modRL("block/fluid/gruel_still");
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture() {
                return SteelShakoMod.modRL("block/fluid/gruel_flowing");
            }
        }, ModFluids.GRUEL_TYPE);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.RED_RAY_BEAM.get(), RayParticleProvider::new);
        event.registerSpriteSet(ModParticles.ORANGE_RAY_BEAM.get(), RayParticleProvider::new);
    }

}
