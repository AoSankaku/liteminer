package net.aosankaku.liteminerdelta.rendering;

import com.iamkaf.amber.api.functions.v1.PlayerFunctions;
import net.aosankaku.liteminerdelta.LiteminerClient;
import net.aosankaku.liteminerdelta.api.event.LiteminerClientEvents;
import net.aosankaku.liteminerdelta.api.event.LiteminerHudContext;
import net.aosankaku.liteminerdelta.api.shape.LiteminerShape;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;

public class HUD {
    private static final int INSUFFICIENT_HUNGER_TEXT_COLOR = 0xFFA500;

    public static void onRenderHUD(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        if (!LiteminerClient.CONFIG.showHUD.get()) {
            return;
        }

        if (Minecraft.getInstance().options.hideGui) {
            return;
        }

        if (!LiteminerClient.isVeinMining()) {
            return;
        }

        LiteminerSelection.Snapshot selection = LiteminerSelection.refresh();
        int selectedBlockCount = selection.blocks().size();
        boolean hasSelectedTarget =
                LiteminerClient.isTargetingABlock() && selectedBlockCount > 0;
        boolean blockedByHunger =
                hasSelectedTarget && LiteminerClient.isBlockedByHunger();

        Font font = Minecraft.getInstance().font;

        float scale = LiteminerClient.CONFIG.hud_scale.get().floatValue();

        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();

        int centerWidth = (int) (width / 2f / scale);
        int centerHeight = (int) (height / 2f / scale);

        LiteminerShape selectedShape = LiteminerClient.shapes.getCurrentItem();
        List<Component> lines = new ArrayList<>();
        if (hasSelectedTarget) {
            lines.add(blockedByHunger
                    ? Component.translatable("hud.liteminer_delta.insufficient_hunger")
                    : Component.translatable(
                            selectedBlockCount > 1
                                    ? "hud.liteminer_delta.selected_blocks"
                                    : "hud.liteminer_delta.selected_blocks_singular",
                            selectedBlockCount
                    ));
        }
        lines.add(selectedShape.displayName());

        LiteminerHudContext context = new LiteminerHudContext(selectedBlockCount, selectedShape, lines);
        LiteminerClientEvents.MODIFY_HUD.invoker().modifyHud(context);
        if (!context.visible() || context.lines().isEmpty()) {
            return;
        }

        int xOffset = (int) (context.xOffset() / scale);
        int yOffset = (int) (context.yOffset() / scale);
        int lineHeight = context.lineHeight();

        Matrix3x2fStack pose = guiGraphics.pose();
//        pose.pushPose();
        pose.pushMatrix();
        pose.scale(scale, scale);

        for (int i = 0; i < context.lines().size(); i++) {
            guiGraphics.text(
                    font,
                    context.lines().get(i),
                    centerWidth + xOffset,
                    centerHeight + yOffset + i * lineHeight,
                    blockedByHunger && i == 0
                            ? INSUFFICIENT_HUNGER_TEXT_COLOR
                            : context.textColor()
            );
        }

        pose.popMatrix();
    }

    public static InteractionResult onMouseScroll(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (LiteminerClient.isVeinMining()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (scrollY != 0) {
                LiteminerClient.cycleShape(scrollY > 0);
            }
            if (!LiteminerClient.CONFIG.showHUD.get()) {
                assert minecraft.player != null;
                PlayerFunctions.sendActionBar(
                        minecraft.player,
                        Component.translatable(
                                "hud.liteminer_delta.changed_shape",
                                LiteminerClient.shapes.getCurrentItem().displayName()
                        )
                );
            }
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }
}
