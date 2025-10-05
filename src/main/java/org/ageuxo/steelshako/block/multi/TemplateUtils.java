package org.ageuxo.steelshako.block.multi;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;

public class TemplateUtils {

    public static boolean placeMultiblock(ServerLevel level, BlockPos pos, Direction direction, MultiBlockType type) {
        StructureTemplateManager manager = level.getStructureManager();
        StructureTemplate template = manager.get(type.location()).orElseThrow();
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(getRotationFromDirection(direction));
        Vec3i size = template.getSize(settings.getRotation());
        BlockPos zeroPositionWithTransform = template.getZeroPositionWithTransform(pos, settings.getMirror(), settings.getRotation());
        Vec3i offset = new Vec3i(-(size.getX() / 2), 0, -(size.getZ() / 2));
        BlockPos centeredPos = zeroPositionWithTransform.offset(offset);
        BoundingBox box = template.getBoundingBox(settings, centeredPos);

        if (structureFits(level, box)){
            boolean placed = template.placeInWorld(level, centeredPos, centeredPos, settings, level.random, Block.UPDATE_ALL);
            if (placed) {
                for (BlockPos placePos : BlockPos.betweenClosed(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ())) {
                    if (level.getBlockEntity(placePos) instanceof MultiblockDelegate delegate) {
                        delegate.initDelegate(centeredPos);
                    }
                }
                return true;
            }
        }

        return false;
    }

    public static boolean placeMultiblockAlt(ServerLevel level, BlockPos pos, Direction direction, MultiBlockType type) {
        StructureTemplateManager manager = level.getStructureManager();
        StructureTemplate template = manager.get(type.location()).orElseThrow();
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(getRotationFromDirection(direction));
        Vec3i size = template.getSize(settings.getRotation());
        BlockPos zeroPositionWithTransform = template.getZeroPositionWithTransform(pos, settings.getMirror(), settings.getRotation());
        Vec3i offset = new Vec3i(-(size.getX() / 2), 0, -(size.getZ() / 2));
        BlockPos centeredPos = zeroPositionWithTransform.offset(offset);
        BoundingBox box = template.getBoundingBox(settings, centeredPos);

        if (structureFits(level, box)){
            boolean placed = template.placeInWorld(level, centeredPos, centeredPos, settings, level.random, Block.UPDATE_ALL);
            if (placed) {
                for (BlockPos placePos : BlockPos.betweenClosed(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ())) {
                    if (level.getBlockEntity(placePos) instanceof MultiblockDelegate delegate) {
                        delegate.initDelegate(centeredPos);
                    }
                }
                return true;
            }
        }

        return false;
    }

    public static Rotation getRotationFromDirection(Direction direction) {
        if (!direction.getAxis().isHorizontal()) throw new IllegalArgumentException("Direction has to be horizontal!");
        Rotation rotation;
        if (direction == Direction.NORTH) {
            rotation = Rotation.NONE;
        } else if (direction == Direction.EAST) {
            rotation = Rotation.CLOCKWISE_90;
        } else if (direction == Direction.SOUTH) {
            rotation = Rotation.CLOCKWISE_180;
        } else {
            rotation = Rotation.COUNTERCLOCKWISE_90;
        }
        return rotation;
    }

    public static boolean structureFits(Level level, BoundingBox box) {
        for (BlockPos blockPos : BlockPos.betweenClosed(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ())) {
            BlockState state = level.getBlockState(blockPos);
            if (!state.isAir() && !state.canBeReplaced()) {
                return false;
            }
        }
        return level.getEntities(null, new AABB(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ())).isEmpty();
    }
}
