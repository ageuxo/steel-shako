package org.ageuxo.steelshako.block.multi;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.ageuxo.steelshako.SteelShakoMod;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum MultiBlockType implements StringRepresentable {
    GRUEL_VAT("multiblock/gruel_vat", 4, 3, 4);

    public static final EnumProperty<MultiBlockType> PROPERTY = EnumProperty.create("multiblock_type", MultiBlockType.class);

    private final ResourceLocation location;
    private final int x;
    private final int y;
    private final int z;

    MultiBlockType(String path, int x, int y, int z) {
        this.location = SteelShakoMod.modRL(path);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ResourceLocation location() {
        return location;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
