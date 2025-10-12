package org.ageuxo.steelshako.render.geo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.ageuxo.steelshako.entity.Automaton;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class AutomatonRenderer extends GeoEntityRenderer<Automaton> {

    public AutomatonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AutomatonModel());
        addRenderLayer(
                new HandLayer<>(this, "right_arm3", "left_arm"));
    }



    public static class HandLayer<E extends LivingEntity & GeoAnimatable> extends BlockAndItemGeoLayer<E> {

        private final String leftHand;
        private final String rightHand;

        public HandLayer(GeoRenderer<E> renderer, String rightHand, String leftHand) {
            super(renderer);
            this.leftHand = leftHand;
            this.rightHand = rightHand;
        }

        @Override
        protected ItemStack getStackForBone(GeoBone bone, E animatable) {
            if (rightHand.equals(bone.getName())) {
                return animatable.getMainHandItem();
            } else if (leftHand.equals(bone.getName())) {
                return animatable.getOffhandItem();
            }
            return null;
        }

        @Override
        protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, E animatable) {
            if (rightHand.equals(bone.getName())) {
                return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            } else if (leftHand.equals(bone.getName())) {
                return ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            }

            return ItemDisplayContext.NONE;
        }

        @Override
        protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, E animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
            /*if (bone.getName().equals(rightHand)) {
                poseStack.pushPose();
                bone.


            }*/


            super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
        }
    }

}
