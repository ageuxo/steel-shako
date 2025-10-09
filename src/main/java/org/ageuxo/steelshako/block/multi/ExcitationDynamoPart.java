package org.ageuxo.steelshako.block.multi;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum ExcitationDynamoPart implements StringRepresentable {
    CORE,
    CHARGER,
    FURNACE,
    TANK,
    PLACEHOLDER;

    public static final EnumProperty<ExcitationDynamoPart> PROPERTY = EnumProperty.create("excitation_dynamo_part", ExcitationDynamoPart.class);

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

}
