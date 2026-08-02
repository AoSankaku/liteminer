package net.aosankaku.liteminerdelta.rendering;

import net.aosankaku.liteminerdelta.Liteminer;
import net.aosankaku.liteminerdelta.LiteminerClient;
import net.aosankaku.liteminerdelta.api.shape.LiteminerShape;
import net.aosankaku.liteminerdelta.shapes.ShapelessWalker;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import java.util.HashSet;
import java.util.Objects;

final class LiteminerSelection {
    private static final SelectionCache cache = new SelectionCache();

    private LiteminerSelection() {}

    static Snapshot refresh() {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        Player player = mc.player;
        if (level == null || player == null) {
            return clear();
        }

        if (!LiteminerClient.isVeinMining()) {
            return clear();
        }

        HitResult result = mc.hitResult;
        if (result == null || result.getType() != HitResult.Type.BLOCK) {
            return clear();
        }

        LiteminerShape shape = LiteminerClient.shapes.getCurrentItem();
        int currentShapeIndex = LiteminerClient.shapes.getCurrentIndex();
        int blockLimit = Liteminer.CONFIG.blockBreakLimit.get();
        boolean distinguishDeepslateOres = LiteminerClient.CONFIG.distinguishDeepslateOres.get();
        boolean distinguishStoneVariants = LiteminerClient.CONFIG.distinguishStoneVariants.get();
        BlockPos origin = ShapelessWalker.raytraceBlock(level, player);

        HashSet<BlockPos> blocks;
        if (cache.isValid(
                origin,
                currentShapeIndex,
                blockLimit,
                distinguishDeepslateOres,
                distinguishStoneVariants
        )) {
            blocks = cache.cachedBlocks;
        } else {
            blocks = shape.walk(
                    level,
                    player,
                    ShapelessWalker.raytrace(level, player).getBlockPos(),
                    distinguishDeepslateOres,
                    distinguishStoneVariants
            );
            if (blocks.isEmpty()) {
                cache.invalidate();
                return snapshot(
                        origin,
                        currentShapeIndex,
                        blockLimit,
                        distinguishDeepslateOres,
                        distinguishStoneVariants,
                        blocks
                );
            }
            cache.update(
                    origin,
                    currentShapeIndex,
                    blockLimit,
                    distinguishDeepslateOres,
                    distinguishStoneVariants,
                    blocks
            );
        }

        return snapshot(
                origin,
                currentShapeIndex,
                blockLimit,
                distinguishDeepslateOres,
                distinguishStoneVariants,
                blocks
        );
    }

    private static Snapshot clear() {
        cache.invalidate();
        return snapshot(null, -1, 0, true, true, new HashSet<>(0));
    }

    private static Snapshot snapshot(BlockPos origin, int shapeIndex, int blockLimit,
            boolean distinguishDeepslateOres, boolean distinguishStoneVariants,
            HashSet<BlockPos> blocks) {
        LiteminerClient.selectedBlocks = blocks;
        return new Snapshot(
                origin,
                shapeIndex,
                blockLimit,
                distinguishDeepslateOres,
                distinguishStoneVariants,
                blocks
        );
    }

    private static long cacheTimeoutMillis(int blockLimit) {
        return blockLimit >= 512 ? 500L :
                blockLimit >= 256 ? 300L :
                blockLimit >= 128 ? 200L :
                100L;
    }

    record Snapshot(
            BlockPos origin,
            int shapeIndex,
            int blockLimit,
            boolean distinguishDeepslateOres,
            boolean distinguishStoneVariants,
            HashSet<BlockPos> blocks
    ) {
        boolean hasBlocks() {
            return !blocks.isEmpty();
        }
    }

    private static final class SelectionCache {
        BlockPos lastOrigin;
        int lastShapeIndex;
        int lastBlockLimit;
        boolean lastDistinguishDeepslateOres;
        boolean lastDistinguishStoneVariants;
        HashSet<BlockPos> cachedBlocks;
        long lastUpdateTime;

        boolean isValid(BlockPos origin, int shapeIndex, int blockLimit,
                boolean distinguishDeepslateOres, boolean distinguishStoneVariants) {
            return cachedBlocks != null &&
                    Objects.equals(lastOrigin, origin) &&
                    lastShapeIndex == shapeIndex &&
                    lastBlockLimit == blockLimit &&
                    lastDistinguishDeepslateOres == distinguishDeepslateOres &&
                    lastDistinguishStoneVariants == distinguishStoneVariants &&
                    (System.currentTimeMillis() - lastUpdateTime) < cacheTimeoutMillis(blockLimit);
        }

        void update(BlockPos origin, int shapeIndex, int blockLimit,
                boolean distinguishDeepslateOres, boolean distinguishStoneVariants,
                HashSet<BlockPos> blocks) {
            this.lastOrigin = origin;
            this.lastShapeIndex = shapeIndex;
            this.lastBlockLimit = blockLimit;
            this.lastDistinguishDeepslateOres = distinguishDeepslateOres;
            this.lastDistinguishStoneVariants = distinguishStoneVariants;
            this.cachedBlocks = blocks;
            this.lastUpdateTime = System.currentTimeMillis();
        }

        void invalidate() {
            this.lastOrigin = null;
            this.cachedBlocks = null;
        }
    }
}
