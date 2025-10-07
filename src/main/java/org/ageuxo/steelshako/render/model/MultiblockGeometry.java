package org.ageuxo.steelshako.render.model;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Function;

@ParametersAreNonnullByDefault
public class MultiblockGeometry implements IUnbakedGeometry<MultiblockGeometry> {

    private final Map<Vec3i, Set<BlockElement>> offsetToElementsMap;
    private final Map<String, Set<BlockElement>> groupElementsMap;

    public MultiblockGeometry(Map<Vec3i, Set<BlockElement>> offsetToElementsMap, Map<String, Set<BlockElement>> groupElementsMap) {
        this.offsetToElementsMap = offsetToElementsMap;
        this.groupElementsMap = groupElementsMap;
    }

    @Override
    public @NotNull BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        Transformation rootTransform = context.getRootTransform();
        if (!rootTransform.isIdentity()) {
            modelState = UnbakedGeometryHelper.composeRootTransformIntoModelState(modelState, rootTransform);
        }

        TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));
        Map<Vec3i, List<BakedQuad>> offsetToQuadsMap = new HashMap<>();
        for (Map.Entry<Vec3i, Set<BlockElement>> entry : offsetToElementsMap.entrySet()) {
            List<BakedQuad> quads = new ArrayList<>();
            for (BlockElement element : entry.getValue()) {
                for (Direction direction : element.faces.keySet()) {
                    BlockElementFace face = element.faces.get(direction);
                    TextureAtlasSprite sprite = spriteGetter.apply(context.getMaterial(face.texture()));
                    BakedQuad quad = BlockModel.bakeFace(element, face, sprite, direction, modelState);
                    quads.add(quad);
                }
            }
            offsetToQuadsMap.put(entry.getKey(), quads);

        }

        return new MultiblockModel(context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(), particle, ItemOverrides.EMPTY, offsetToQuadsMap);
    }

}
