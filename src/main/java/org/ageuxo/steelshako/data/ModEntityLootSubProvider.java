package org.ageuxo.steelshako.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.ageuxo.steelshako.entity.ModEntityTypes;
import org.ageuxo.steelshako.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class ModEntityLootSubProvider extends EntityLootSubProvider {

    protected ModEntityLootSubProvider(HolderLookup.Provider registries) {
        super(FeatureFlags.DEFAULT_FLAGS, registries);
    }

    @Override
    public void generate() {
        this.add(ModEntityTypes.AUTOMATON.get(),
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .add(
                                                LootItem.lootTableItem(ModItems.VACUUM_TUBE)
                                                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1)))
                                        )
                        )
        );


    }

    @Override
    protected @NotNull Stream<EntityType<?>> getKnownEntityTypes() {
        return ModEntityTypes.ENTITIES.getEntries().stream().map(DeferredHolder::value);
    }
}
