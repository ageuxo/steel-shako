package org.ageuxo.steelshako.block.multi;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.ageuxo.steelshako.SteelShakoMod;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.util.Locale;

public enum MultiBlockType implements StringRepresentable {
    GRUEL_VAT("multiblock/gruel_vat", 4, 3, 4),
    EXCITATION_DYNAMO("multiblock/excitation_dynamo", 3, 3, 3);

    private final ResourceLocation location;
    private final Vector3i size;

    MultiBlockType(String path, int x, int y, int z) {
        this.location = SteelShakoMod.modRL(path);
        this.size = new Vector3i(x, y, z);
    }

    public ResourceLocation location() {
        return location;
    }

    public Vector3i size() {
        return size;
    }

    public int x() {
        return size.x;
    }

    public int y() {
        return size.y;
    }

    public int z() {
        return size.z;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
