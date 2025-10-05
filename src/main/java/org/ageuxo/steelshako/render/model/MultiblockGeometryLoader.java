package org.ageuxo.steelshako.render.model;

import com.google.gson.*;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import org.ageuxo.steelshako.SteelShakoMod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class MultiblockGeometryLoader implements IGeometryLoader<MultiblockGeometry> {

    public static final MultiblockGeometryLoader INSTANCE = new MultiblockGeometryLoader();
    public static final ResourceLocation ID = SteelShakoMod.modRL("multiblocks");

    private MultiblockGeometryLoader() { }

    public static Group readGroup(JsonObject group) {
        JsonArray children = group.get("children").getAsJsonArray();
        List<Integer> childIndices = new ArrayList<>();
        children.asList().forEach(e -> childIndices.add(e.getAsInt()));
        return new Group(group.get("name").getAsString(), childIndices);
    }

    @Override
    public @NotNull MultiblockGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {

        List<BlockElement> elements = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(jsonObject, "elements")) {
            elements.add(deserializationContext.deserialize(element, BlockElement.class));
        }

        Map<String, Set<BlockElement>> groupElementsMap = new HashMap<>();
        for (JsonElement jsonGroup : GsonHelper.getAsJsonArray(jsonObject, "groups")) {
            Group group = readGroup(jsonGroup.getAsJsonObject());
            Set<BlockElement> set = groupElementsMap.getOrDefault(group.name, new HashSet<>());
            for (int childIndex : group.childIndices) {
                set.add(elements.get(childIndex));
            }
            groupElementsMap.put(group.name, set);
        }

        Map<Vec3i, Set<BlockElement>> offsetToElementsMap = new HashMap<>();
        for (Map.Entry<String, JsonElement> jsonPosEntry : jsonObject.getAsJsonObject("group_positions").entrySet()) {
            JsonObject posObj = jsonPosEntry.getValue().getAsJsonObject();
            int x = posObj.get("x").getAsInt();
            int y = posObj.get("y").getAsInt();
            int z = posObj.get("z").getAsInt();
            Set<BlockElement> value = groupElementsMap.get(jsonPosEntry.getKey());
            if (value == null) throw new JsonParseException("'group_positions' missing offset mapping for '%s' group!".formatted(jsonPosEntry.getKey()));
            offsetToElementsMap.put(new Vec3i(x / 8, y / 8, z / 8), value);
        }

        return new MultiblockGeometry(offsetToElementsMap, groupElementsMap);
    }

    public static class Group {
        private final String name;
        private final List<Integer> childIndices;

        protected Group(String name, List<Integer> childIndices) {
            this.name = name;
            this.childIndices = childIndices;
        }

        public String name() {
            return name;
        }

        public List<Integer> childIndices() {
            return childIndices;
        }
    }


}
