package org.ageuxo.steelshako;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static final TagKey<Block> IS_MANGALAN = TagKey.create(Registries.BLOCK, SteelShakoMod.modRL("is_mangalan"));

    public static final TagKey<Item> LASER_AMMO = TagKey.create(Registries.ITEM, SteelShakoMod.modRL("laser_ammo"));

    public static final TagKey<EntityType<?>> AUTOMATON_MOB_TARGETS = TagKey.create(Registries.ENTITY_TYPE, SteelShakoMod.modRL("automaton_mob_targets"));

    public static final TagKey<Biome> HAS_MANGALAN_SPRINGS = TagKey.create(Registries.BIOME, SteelShakoMod.modRL("has_mangalan_springs"));
    public static final TagKey<Biome> SPAWNS_AUTOMATA = TagKey.create(Registries.BIOME, SteelShakoMod.modRL("spawns_automata"));
}
