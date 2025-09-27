package org.ageuxo.steelshako.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.HumanoidArm;

public class ItemHandPoses {

    // ItemInHandRenderer has this as a private method
    public static void applyItemArmTransform(PoseStack poseStack, HumanoidArm hand, float equippedProg) {
        int i = hand == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate((float)i * 0.56F, -0.52F + equippedProg * -0.6F, -0.72F);
    }

}
