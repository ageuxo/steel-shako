package org.ageuxo.steelshako.data;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.ageuxo.steelshako.block.GruelShroomBlock;
import org.ageuxo.steelshako.block.ModBlocks;
import org.ageuxo.steelshako.block.multi.ExcitationDynamoPart;
import org.ageuxo.steelshako.block.multi.VatPart;
import org.ageuxo.steelshako.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Supplier;

public class ModBlockLootSubProvider extends BlockLootSubProvider {

    protected ModBlockLootSubProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
    }

    @Override
    protected void generate() {
        this.add(ModBlocks.GRUEL_FLUID.get(), noDrop());
        this.add(ModBlocks.MANGALAN_FLUID.get(), noDrop());

        this.add(ModBlocks.GRUEL_SHROOM.get(),
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .add(
                                                this.applyExplosionDecay(
                                                        ModBlocks.GRUEL_SHROOM,
                                                        LootItem.lootTableItem(ModItems.GRUEL_SPORES)
                                                                .apply(
                                                                        SetItemCountFunction.setCount(UniformGenerator.between(1, 3))
                                                                                .when(
                                                                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.GRUEL_SHROOM.get())
                                                                                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(GruelShroomBlock.AGE, 3))
                                                                                )
                                                                )
                                                )
                                        )
                        )
        );

        itemDropOnStateProperty(ModBlocks.EXCITATION_DYNAMO_BLOCK, ModItems.EXCITATION_DYNAMO_DEPLOYER, ExcitationDynamoPart.PROPERTY, ExcitationDynamoPart.CORE);
        itemDropOnStateProperty(ModBlocks.VAT_BLOCK, ModItems.VAT_DEPLOYER, VatPart.PROPERTY, VatPart.CORE);
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries()
                .stream()
                .map(e -> (Block) e.value())
                .toList();
    }

    private <T extends Comparable<T> & StringRepresentable> void itemDropOnStateProperty(Supplier<? extends Block> block, ItemLike item, Property<T> property, T value) {
        this.add(block.get(),
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .add(
                                                LootItem.lootTableItem(item)
                                                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                                        .when(
                                                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(block.get())
                                                                        .setProperties(
                                                                                StatePropertiesPredicate.Builder.properties()
                                                                                        .hasProperty(property, value)
                                                                        )
                                                        )
                                        )
                        )
        );
    }
}
