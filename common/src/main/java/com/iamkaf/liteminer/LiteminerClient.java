package com.iamkaf.liteminer;

import com.iamkaf.liteminer.config.LiteminerClientConfig;
import com.iamkaf.liteminer.networking.LiteminerNetwork;
import com.iamkaf.liteminer.rendering.HUD;
import com.iamkaf.liteminer.shapes.Cycler;
import com.iamkaf.liteminer.shapes.Walker;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;

public class LiteminerClient {
    public static final int PACKET_DELAY = 125;
    public static final KeyMapping KEY_MAPPING = new KeyMapping("key.liteminer.veinmine",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            "key.categories.liteminer"
    );
    public static final LiteminerClientConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;
    public static HashSet<BlockPos> selectedBlocks = HashSet.newHashSet(0);
    public static Minecraft mc;
    public static Cycler<Walker> shapes = new Cycler<>(Liteminer.WALKERS);
    private static boolean currentState = false;
    private static boolean hungerRequired = true;
    private static boolean lastDistinguishDeepslateOres = true;
    private static long lastChange = System.currentTimeMillis();

    static {
        Pair<LiteminerClientConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(LiteminerClientConfig::new);
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public static void init() {
        mc = Minecraft.getInstance();
        KeyMappingRegistry.register(KEY_MAPPING);
        ClientTickEvent.CLIENT_POST.register(LiteminerClient::onPostTick);
        ClientGuiEvent.RENDER_HUD.register(HUD::onRenderHUD);
        ClientRawInputEvent.MOUSE_SCROLLED.register(HUD::onMouseScroll);
    }

    public static void onPostTick(Minecraft minecraft) {
        boolean distinguishDeepslateOres = CONFIG.distinguishDeepslateOres.get();
        if (distinguishDeepslateOres != lastDistinguishDeepslateOres) {
            lastDistinguishDeepslateOres = distinguishDeepslateOres;
            syncStateToServer();
        }

        if ((System.currentTimeMillis() - getLastChange()) < PACKET_DELAY) {
            return;
        }

        switch (CONFIG.keyMode.get()) {
            case HOLD -> {
                var newState = KEY_MAPPING.isDown();

                if (newState == isVeinMining()) {
                    return;
                }

                sendStateToServer(newState);
                currentState = newState;
            }
            case TOGGLE -> {
                if (KEY_MAPPING.consumeClick()) {
                    var newState = !isVeinMining();
                    sendStateToServer(newState);
                    currentState = newState;
                }
            }
        }
    }

    public static boolean isVeinMining() {
        return currentState;
    }

    public static void sendStateToServer(boolean keybindState) {
        new LiteminerNetwork.Messages.C2SVeinmineKeybindChange(keybindState,
                shapes.getCurrentIndex(),
                CONFIG.distinguishDeepslateOres.get()
        ).sendToServer();
    }

    public static void syncStateToServer() {
        if (mc == null || mc.getConnection() == null) {
            return;
        }
        sendStateToServer(isVeinMining());
    }

    public static boolean isBlockedByHunger() {
        return mc != null
                && mc.player != null
                && !mc.player.isCreative()
                && hungerRequired
                && mc.player.getFoodData().getFoodLevel() <= 0;
    }

    public static void setHungerRequired(boolean hungerRequired) {
        LiteminerClient.hungerRequired = hungerRequired;
    }

    public static long getLastChange() {
        return lastChange;
    }

    public static void setLastChange(long lastChange) {
        LiteminerClient.lastChange = lastChange;
    }

    public static boolean isTargetingABlock() {
        HitResult result = mc.hitResult;
        return result != null && result.getType() == HitResult.Type.BLOCK;
    }
}
