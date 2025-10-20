package org.ageuxo.steelshako.block;

import net.minecraft.world.level.block.LiquidBlock;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.block.multi.ExcitationDynamoBlock;
import org.ageuxo.steelshako.block.multi.VatBlock;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SteelShakoMod.MOD_ID);

    public static final DeferredBlock<VatBlock> VAT_BLOCK = BLOCKS.registerBlock("vat_block", VatBlock::new);
    public static final DeferredBlock<ExcitationDynamoBlock> EXCITATION_DYNAMO_BLOCK = BLOCKS.registerBlock("excitation_dynamo", ExcitationDynamoBlock::new);

    public static final DeferredBlock<LiquidBlock> GRUEL_FLUID = BLOCKS.registerBlock("gruel", (p)-> new LiquidBlock(ModFluids.GRUEL.get(), p.replaceable()));
    public static final DeferredBlock<LiquidBlock> MANGALAN_FLUID = BLOCKS.registerBlock("mangalan", (p)-> new LiquidBlock(ModFluids.MANGALAN.get(), p.replaceable()));

    public static final DeferredBlock<GruelShroomBlock> GRUEL_SHROOM = BLOCKS.registerBlock("gruel_shroom", GruelShroomBlock::new);

}
