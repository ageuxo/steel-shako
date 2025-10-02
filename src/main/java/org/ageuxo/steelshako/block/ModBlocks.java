package org.ageuxo.steelshako.block;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.block.multi.VatBlock;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SteelShakoMod.MOD_ID);

    public static final DeferredBlock<VatBlock> VAT_BLOCK = BLOCKS.registerBlock("vat_block", VatBlock::new);

}
