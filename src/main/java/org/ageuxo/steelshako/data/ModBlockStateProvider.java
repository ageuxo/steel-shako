package org.ageuxo.steelshako.data;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.block.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SteelShakoMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        getVariantBuilder(ModBlocks.VAT_BLOCK.get())
                .forAllStates(state -> {
                    Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

                    return ConfiguredModel.builder()
                            .modelFile(models().getExistingFile(SteelShakoMod.modRL("block/multiblock/gruelshroom_vat")))
                            .rotationY((int) facing.toYRot())
                            .build();

                });


    }

}
