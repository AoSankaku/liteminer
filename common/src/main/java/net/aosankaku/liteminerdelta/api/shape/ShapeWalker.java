package net.aosankaku.liteminerdelta.api.shape;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashSet;

/**
 * Calculates the block positions included by a Liteminer shape.
 */
@FunctionalInterface
public interface ShapeWalker {
    /**
     * Calculates candidate positions for a veinmine operation.
     *
     * <p>The returned set should usually include {@code origin}. Liteminer skips the origin
     * when processing secondary blocks, but including it keeps highlighting and count behavior
     * consistent with built-in shapes.</p>
     *
     * @param level  the level where the shape is being evaluated
     * @param player the player using Liteminer
     * @param origin the block that started the operation
     * @return candidate block positions for the shape
     */
    HashSet<BlockPos> walk(Level level, Player player, BlockPos origin);

    /**
     * Calculates candidate positions with the local ore-host matching preference.
     * Custom shapes remain source-compatible and ignore the preference unless they override this method.
     */
    default HashSet<BlockPos> walk(Level level, Player player, BlockPos origin,
            boolean distinguishDeepslateOres) {
        return walk(level, player, origin);
    }

    /**
     * Calculates candidate positions with local ore-host and base-stone matching preferences.
     * Custom shapes remain source-compatible and ignore new preferences unless they override this method.
     */
    default HashSet<BlockPos> walk(Level level, Player player, BlockPos origin,
            boolean distinguishDeepslateOres, boolean distinguishStoneVariants) {
        return walk(level, player, origin, distinguishDeepslateOres);
    }
}
