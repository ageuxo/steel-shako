package org.ageuxo.steelshako;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class ModTags {

    public static final TagKey<Item> LASER_AMMO = TagKey.create(Registries.ITEM, SteelShakoMod.modRL("laser_ammo"));

    public static final TagKey<EntityType<?>> AUTOMATON_MOB_TARGETS = TagKey.create(Registries.ENTITY_TYPE, SteelShakoMod.modRL("automaton_mob_targets"));
}
