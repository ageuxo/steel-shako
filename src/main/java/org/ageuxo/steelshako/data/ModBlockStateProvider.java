package org.ageuxo.steelshako.data;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.block.ModBlocks;

import java.util.function.Supplier;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SteelShakoMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        multiblockFacingModel("block/multiblock/gruelshroom_vat", ModBlocks.VAT_BLOCK, 180);
        multiblockFacingModel("block/multiblock/excitation_dynamo", ModBlocks.EXCITATION_DYNAMO_BLOCK, 90);
    }

    private void multiblockFacingModel(String path, Supplier<? extends Block> block, int offset) {
        getVariantBuilder(block.get())
                .forAllStates(state -> {
                    Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

                    return ConfiguredModel.builder()
                            .modelFile(models().getExistingFile(SteelShakoMod.modRL(path)))
                            .rotationY(((int) facing.toYRot() + offset) % 360)
                            .build();

                });
    }

}
