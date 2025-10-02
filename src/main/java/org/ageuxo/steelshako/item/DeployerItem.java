package org.ageuxo.steelshako.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import org.ageuxo.steelshako.block.multi.MultiBlockType;
import org.ageuxo.steelshako.block.multi.TemplateUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DeployerItem extends Item {

    private final MultiBlockType multiBlock;

    public DeployerItem(Properties properties, MultiBlockType multiBlock) {
        super(properties);
        this.multiBlock = multiBlock;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        return this.place(new BlockPlaceContext(context));
    }

    public InteractionResult place(BlockPlaceContext context) {
        if (context.getLevel() instanceof ServerLevel serverLevel &&
                TemplateUtils.placeMultiblock(serverLevel, context.getClickedPos(), context.getHorizontalDirection(), multiBlock)) {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
