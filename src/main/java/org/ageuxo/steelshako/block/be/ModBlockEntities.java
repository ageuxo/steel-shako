package org.ageuxo.steelshako.block.be;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.block.ModBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SteelShakoMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VatBlockEntity>> GRUEL_VAT = register("gruel_vat", VatBlockEntity::new, ModBlocks.VAT_BLOCK);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DelegatingBlockEntity>> VAT_PLACEHOLDER = register("vat_placeholder", DelegatingBlockEntity::gruelVat, ModBlocks.VAT_BLOCK);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExcitationDynamoBlockEntity>> EXCITATION_DYNAMO = register("excitation_dynamo", ExcitationDynamoBlockEntity::new, ModBlocks.EXCITATION_DYNAMO_BLOCK);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DelegatingBlockEntity>> EXCITATION_PLACEHOLDER = register("excitation_placeholder", DelegatingBlockEntity::excitationDynamo, ModBlocks.EXCITATION_DYNAMO_BLOCK);

    @SafeVarargs
    private static @NotNull <T extends BlockEntity, B extends Block & EntityBlock> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> blockEntitySupplier, DeferredBlock<B>... validBlocks) {
        //noinspection DataFlowIssue
        return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(blockEntitySupplier, Arrays.stream(validBlocks).map(DeferredBlock::get).toArray(Block[]::new)).build(null));
    }


}
