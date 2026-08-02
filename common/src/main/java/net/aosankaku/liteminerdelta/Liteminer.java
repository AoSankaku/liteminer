package net.aosankaku.liteminerdelta;

import net.aosankaku.liteminerdelta.config.LiteminerConfig;
import net.aosankaku.liteminerdelta.event.Events;
import net.aosankaku.liteminerdelta.networking.LiteminerNetwork;
import net.aosankaku.liteminerdelta.shapes.*;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class Liteminer {
    public static final String MOD_ID = "liteminer_delta";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final LiteminerConfig CONFIG;
    public static final ForgeConfigSpec CONFIG_SPEC;
    public static final List<Walker> WALKERS = List.of(new ShapelessWalker(),
            new TunnelWalker(),
            new StaircaseUpWalker(),
            new StaircaseDownWalker(),
            new ThreeByThreeWalker()
    );
    public static Liteminer instance;

    static {
        Pair<LiteminerConfig, ForgeConfigSpec> pair =
                new ForgeConfigSpec.Builder().configure(LiteminerConfig::new);
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public Map<UUID, LiteminerPlayerState> playerStateMap = new HashMap<>();

    public Liteminer() {
        Liteminer.instance = this;
    }

    public static void init() {
        LOGGER.info("Litemining, from poppies to deepslate.");

        LiteminerNetwork.init();
        Events.init();
    }

    /**
     * Creates resource location in the mod namespace with the given path.
     */
    public static ResourceLocation resource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static float getScaledBreakSpeedModifier(int blockCount) {
        float modifier = CONFIG.harvestTimePerBlockModifier.get().floatValue();
        float blockBreakLimit = CONFIG.blockBreakLimit.get().floatValue();

        return 1 - blockCount / blockBreakLimit * modifier * 0.1f * 0.95f;
    }

    public LiteminerPlayerState getPlayerState(ServerPlayer player) {
        return playerStateMap.computeIfAbsent(player.getUUID(), LiteminerPlayerState::new);
    }

    public void onKeymappingStateChange(ServerPlayer player, boolean keybindState, int shape,
            boolean distinguishDeepslateOres, boolean distinguishStoneVariants) {
        var playerState = getPlayerState(player);
        playerState.setKeymappingState(keybindState);
        playerState.setShape(shape);
        playerState.setDistinguishDeepslateOres(distinguishDeepslateOres);
        playerState.setDistinguishStoneVariants(distinguishStoneVariants);
    }

    public float onBreakSpeed(ServerPlayer player, float originalSpeed) {
        LiteminerPlayerState playerState = getPlayerState(player);
        var isVeinMining = playerState.getKeymappingState();

        if (isVeinMining) {
            Level level = player.level();
            int blockCount = WALKERS.get(playerState.getShape())
                    .walk(level,
                            player,
                            ShapelessWalker.raytrace(level, player).getBlockPos(),
                            playerState.getDistinguishDeepslateOres(),
                            playerState.getDistinguishStoneVariants()
                    )
                    .size();
            return getScaledBreakSpeedModifier(blockCount);
        }
        return 1f;
    }
}
