package org.ageuxo.steelshako.block.multi;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum VatPart implements StringRepresentable {
    CORE,
    FURNACE,
    TANK,
    TAP,
    VAT,
    PLACEHOLDER;

    public static final EnumProperty<VatPart> PROPERTY = EnumProperty.create("vat_part", VatPart.class);

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

}
