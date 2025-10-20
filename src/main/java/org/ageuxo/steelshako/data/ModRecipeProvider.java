package org.ageuxo.steelshako.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.ageuxo.steelshako.item.ModItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;
@ParametersAreNonnullByDefault
public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MINING_RAY_GUN)
                .define('V', ModItems.VACUUM_TUBE)
                .define('B', Blocks.IRON_BLOCK)
                .define('P', ItemTags.PLANKS)
                .define('C', ModItems.CRYSTAL.get())
                .pattern(" VV")
                .pattern("CBP")
                .pattern("VVP")
                .unlockedBy("has_vaccum_tube", has(ModItems.VACUUM_TUBE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.VAT_DEPLOYER)
                .define('I', Items.IRON_INGOT)
                .define('L', ItemTags.LOGS)
                .define('C', Blocks.CAULDRON)
                .define('F', Blocks.FURNACE)
                .pattern("IIC")
                .pattern("IIF")
                .pattern("LCL")
                .unlockedBy("has_gruel_spores", has(ModItems.GRUEL_SPORES))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EXCITATION_DYNAMO_DEPLOYER)
                .define('B', Items.COPPER_BLOCK)
                .define('C', Items.CAULDRON)
                .define('F', Items.FURNACE)
                .define('I', Items.IRON_INGOT)
                .define('S', Items.STONE_SLAB)
                .pattern("BIB")
                .pattern("BIB")
                .pattern("CSF")
                .unlockedBy("has_furnace", has(Items.FURNACE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CRYSTAL)
                .define('M', ModItems.MANGALAN_BUCKET)
                .define('R', Items.REDSTONE)
                .pattern("RRR")
                .pattern("RMR")
                .pattern("RRR")
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(recipeOutput);


    }


}
