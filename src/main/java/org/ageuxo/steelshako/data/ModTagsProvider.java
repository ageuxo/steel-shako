package org.ageuxo.steelshako.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.ageuxo.steelshako.ModTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModTagsProvider {

    public static class Block extends BlockTagsProvider {

        public Block(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, modId, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {

        }
    }

    public static class Item extends ItemTagsProvider {

        public Item(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<net.minecraft.world.level.block.Block>> blockTags, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, blockTags, modId, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            this.tag(ModTags.LASER_AMMO)
                    .add(Items.REDSTONE);
        }
    }

    public static class Entity extends EntityTypeTagsProvider {

        public Entity(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, provider, modId, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            this.tag(ModTags.AUTOMATON_MOB_TARGETS)
                    .add(EntityType.PLAYER)
                    .add(EntityType.VILLAGER)
                    .add(EntityType.IRON_GOLEM)
                    .addTag(EntityTypeTags.ILLAGER);
        }

    }

    public static class Biomes extends BiomeTagsProvider {

        public Biomes(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, provider, modId, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            this.tag(ModTags.HAS_MANGALAN_SPRINGS)
                    .addTag(Tags.Biomes.IS_OVERWORLD);
            this.tag(ModTags.SPAWNS_AUTOMATA)
                    .addTag(Tags.Biomes.IS_OVERWORLD)
                    .remove(net.minecraft.world.level.biome.Biomes.MUSHROOM_FIELDS);
        }
    }
}
