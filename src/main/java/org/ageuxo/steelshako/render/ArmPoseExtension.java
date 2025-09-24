package org.ageuxo.steelshako.render;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public class ArmPoseExtension {
    public static final EnumProxy<HumanoidModel.ArmPose> RAYGUN_ARMPOSE_PROXY = new EnumProxy<>(
            HumanoidModel.ArmPose.class, true,
            (IArmPoseTransformer) (model, entity, arm) -> {
                boolean rightHanded = arm == HumanoidArm.RIGHT;
                ModelPart primary = rightHanded ? model.rightArm : model.leftArm;
                ModelPart secondary = rightHanded ? model.leftArm : model.rightArm;
                ModelPart head = model.head;
                primary.yRot = (rightHanded ? -0.1F : 0.1F) + head.yRot;
                secondary.yRot = (rightHanded ? 0.6F : -0.6F) + head.yRot;
                primary.xRot = (float) (-Math.PI / 2) + head.xRot + 0.1F;
                secondary.xRot = -1.5F + head.xRot;
            }
    );
}
