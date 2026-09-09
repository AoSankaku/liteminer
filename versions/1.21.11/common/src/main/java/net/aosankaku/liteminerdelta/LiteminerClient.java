package net.aosankaku.liteminerdelta;

import com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents;
import com.iamkaf.amber.api.functions.v1.PlayerFunctions;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientTickEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.HudEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.InputEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.RenderEvents;
import com.iamkaf.amber.api.registry.v1.KeybindHelper;
import net.aosankaku.liteminerdelta.api.shape.LiteminerShape;
import net.aosankaku.liteminerdelta.api.shape.LiteminerShapes;
import net.aosankaku.liteminerdelta.config.LiteminerClientConfig;
import net.aosankaku.liteminerdelta.networking.C2SVeinmineKeybindChange;
import net.aosankaku.liteminerdelta.networking.LiteminerNetwork;
import net.aosankaku.liteminerdelta.rendering.BlockHighlightRenderer;
import net.aosankaku.liteminerdelta.rendering.HUD;
import net.aosankaku.liteminerdelta.shapes.Cycler;
import net.aosankaku.liteminerdelta.tags.LiteminerTags;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.util.HashSet;

public class LiteminerClient {
    public static final int PACKET_DELAY = 125;
    public static final KeyMapping.Category KEY_CATEGORY =
            KeyMapping.Category.register(Constants.resource(Constants.MOD_ID));
    public static final KeyMapping KEY_MAPPING =
            new KeyMapping("key.liteminer_delta.veinmine", InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), KEY_CATEGORY);
    public static final LiteminerClientConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;
    public static HashSet<BlockPos> selectedBlocks = HashSet.newHashSet(0);
    public static Cycler<LiteminerShape> shapes = new Cycler<>(LiteminerShapes.all());
    private static final ClientActivationGate ACTIVATION_GATE = new ClientActivationGate();
    private static boolean keybindState = false;
    private static boolean currentState = false;
    private static boolean hungerRequired = true;
    private static boolean lastDistinguishDeepslateOres = true;
    private static boolean lastDistinguishStoneVariants = true;
    private static long lastChange = System.currentTimeMillis();
    private static Runnable openConfigScreenCallback;
    private static boolean pendingConfigScreenOpen;

    static {
        Pair<LiteminerClientConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(LiteminerClientConfig::new);
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public static void init() {
        KeybindHelper.register(KEY_MAPPING);
        ClientTickEvents.END_CLIENT_TICK.register(LiteminerClient::onPostTick);
        HudEvents.RENDER_HUD.register(HUD::onRenderHUD);
        InputEvents.MOUSE_SCROLL_PRE.register(HUD::onMouseScroll);
        RenderEvents.BLOCK_OUTLINE_RENDER.register(BlockHighlightRenderer::renderLiteminerHighlight);
        ClientCommandEvents.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(Commands.literal("liteminer_delta")
                        .executes(context -> openConfigCommand())
                        .then(Commands.literal("config")
                                .executes(context -> openConfigCommand()))
                        .then(Commands.literal("shape")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("index", IntegerArgumentType.integer(0))
                                                .executes(context -> setShapeCommand(
                                                        IntegerArgumentType.getInteger(context, "index")
                                                )))))));
    }

    public static void onPostTick() {
        openPendingConfigScreen();

        boolean distinguishDeepslateOres = CONFIG.distinguishDeepslateOres.get();
        boolean distinguishStoneVariants = CONFIG.distinguishStoneVariants.get();
        if (distinguishDeepslateOres != lastDistinguishDeepslateOres
                || distinguishStoneVariants != lastDistinguishStoneVariants) {
            lastDistinguishDeepslateOres = distinguishDeepslateOres;
            lastDistinguishStoneVariants = distinguishStoneVariants;
            syncStateToServer();
        }

        if ((System.currentTimeMillis() - getLastChange()) < PACKET_DELAY) {
            return;
        }

        boolean previousKeybindState = keybindState;
        switch (CONFIG.keyMode.get()) {
            case HOLD -> keybindState = KEY_MAPPING.isDown();
            case TOGGLE -> {
                if (KEY_MAPPING.consumeClick()) {
                    keybindState = !keybindState;
                }
            }
        }
        boolean keybindStateChanged = keybindState != previousKeybindState;

        boolean newState = keybindState || isTargetingOreAutomatically();
        if (!newState) {
            ACTIVATION_GATE.reset();
        } else if (!isActivationAllowed(keybindState)) {
            return;
        }
        if (newState == isVeinMining()) {
            return;
        }

        sendStateToServer(newState);
        currentState = newState;
        if (keybindStateChanged) {
            playModeToggleSound(newState);
        }
    }

    private static void playModeToggleSound(boolean enabled) {
        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, enabled ? 1.2F : 0.8F)
        );
    }

    private static boolean isActivationAllowed(boolean requestedByKeybind) {
        ClientActivationGate.Result result = ACTIVATION_GATE.evaluate(
                requestedByKeybind,
                LiteminerNetwork.isServerSupported()
        );
        if (result == ClientActivationGate.Result.ALLOW) {
            return true;
        }

        currentState = false;
        if (result == ClientActivationGate.Result.BLOCK_AND_NOTIFY) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
                minecraft.player.displayClientMessage(
                        Component.translatable("message.liteminer_delta.server_unavailable"),
                        true
                );
            }
        }
        return false;
    }

    public static boolean isVeinMining() {
        return currentState;
    }

    public static void sendStateToServer(boolean keybindState) {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        LiteminerNetwork.sendToServer(new C2SVeinmineKeybindChange(
                keybindState,
                shapes.getCurrentIndex(),
                CONFIG.distinguishDeepslateOres.get(),
                CONFIG.distinguishStoneVariants.get()
        ));
    }

    public static void syncStateToServer() {
        sendStateToServer(isVeinMining());
    }

    public static boolean isBlockedByHunger() {
        return Minecraft.getInstance().player != null
                && !Minecraft.getInstance().player.isCreative()
                && hungerRequired
                && Minecraft.getInstance().player.getFoodData().getFoodLevel() <= 0;
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
        HitResult result = Minecraft.getInstance().hitResult;
        return result != null && result.getType() == HitResult.Type.BLOCK;
    }

    private static boolean isTargetingOreAutomatically() {
        if (!CONFIG.autoVeinMineOres.get()) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || !(minecraft.hitResult instanceof BlockHitResult blockHitResult)) {
            return false;
        }

        return minecraft.level.getBlockState(blockHitResult.getBlockPos()).is(LiteminerTags.Blocks.ORES);
    }

    public static Runnable getOpenConfigScreenCallback() {
        return openConfigScreenCallback;
    }

    public static void openConfigScreen() {
        pendingConfigScreenOpen = true;
    }

    private static void openPendingConfigScreen() {
        if (!pendingConfigScreenOpen) {
            return;
        }

        pendingConfigScreenOpen = false;
        if (openConfigScreenCallback != null) {
            openConfigScreenCallback.run();
            return;
        }

        showConfigScreenUnavailableMessage(null);
    }

    public static void showConfigScreenUnavailableMessage(Path configDirectory) {
        if (Minecraft.getInstance().player != null) {
            Component message = Component.literal("Liteminer Delta's config screen is not available on this loader.");
            if (configDirectory != null) {
                message = Component.empty()
                        .append(message)
                        .append(Component.literal(" "))
                        .append(Component.literal("[Open config folder]").withStyle(style -> style
                                .withUnderlined(true)
                                .withClickEvent(new ClickEvent.OpenFile(configDirectory))
                                .withHoverEvent(new HoverEvent.ShowText(Component.literal(configDirectory.toString())))));
            }

            PlayerFunctions.sendMessage(Minecraft.getInstance().player, message);
        }
    }

    private static int openConfigCommand() {
        openConfigScreen();
        return 1;
    }

    private static int setShapeCommand(int index) {
        shapes.setCurrentIndex(index);
        sendStateToServer(isVeinMining());
        return 1;
    }

    public static void setOpenConfigScreenCallback(Runnable callback) {
        openConfigScreenCallback = callback;
    }
}
