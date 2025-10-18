package org.ageuxo.steelshako.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BushBlock;
import org.jetbrains.annotations.NotNull;

public class GruelShroomBlock extends BushBlock {

    public static final MapCodec<GruelShroomBlock> CODEC = simpleCodec(GruelShroomBlock::new);

    protected GruelShroomBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }
}
