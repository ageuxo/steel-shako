package org.ageuxo.steelshako;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {

    public static final TagKey<Item> LASER_AMMO = TagKey.create(Registries.ITEM, SteelShakoMod.modRL("laser_ammo"));
}
