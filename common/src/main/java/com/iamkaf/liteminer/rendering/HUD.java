package com.iamkaf.liteminer.rendering;

import com.iamkaf.amber.api.player.FeedbackHelper;
import com.iamkaf.liteminer.LiteminerClient;
import com.iamkaf.liteminer.networking.LiteminerNetwork;
import dev.architectury.event.EventResult;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class HUD {
    private static final int DEFAULT_TEXT_COLOR = 0xFFFFFF;
    private static final int INSUFFICIENT_HUNGER_TEXT_COLOR = 0xFFA500;

    public static void onRenderHUD(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!LiteminerClient.CONFIG.showHUD.get()) {
            return;
        }

        if (Minecraft.getInstance().options.hideGui) {
            return;
        }

        if (!LiteminerClient.isVeinMining()) {
            return;
        }

        int selectedBlockCount = LiteminerClient.selectedBlocks.size();
        boolean hasSelectedTarget =
                LiteminerClient.isTargetingABlock() && selectedBlockCount > 0;

        Font font = LiteminerClient.mc.font;

        int lineHeight = 10;
        float scale = LiteminerClient.CONFIG.hud_scale.get().floatValue();

        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();

        int centerWidth = (int) (width / 2f / scale);
        int centerHeight = (int) (height / 2f / scale);

        int xOffset = (int) (5 / scale);
        int yOffset = (int) (-10 / scale);

        var pose = guiGraphics.pose();
        pose.pushPose();
        pose.scale(scale, scale, 1f);

        if (hasSelectedTarget) {
            boolean blockedByHunger = LiteminerClient.isBlockedByHunger();
            Component selectedBlocksLabel = blockedByHunger
                    ? Component.translatable("hud.liteminer.insufficient_hunger")
                    : Component.translatable(
                            selectedBlockCount > 1
                                    ? "hud.liteminer.selected_blocks"
                                    : "hud.liteminer.selected_blocks_singular",
                            selectedBlockCount
                    );
            int selectedBlocksLabelColor =
                    blockedByHunger ? INSUFFICIENT_HUNGER_TEXT_COLOR : DEFAULT_TEXT_COLOR;

            guiGraphics.drawString(font,
                    selectedBlocksLabel,
                    centerWidth + xOffset,
                    centerHeight + yOffset,
                    selectedBlocksLabelColor
            );
        }

        int shapeYOffset = hasSelectedTarget ? yOffset + lineHeight : yOffset;
        guiGraphics.drawString(font,
                LiteminerClient.shapes.getCurrentItem().getDisplayName(),
                centerWidth + xOffset,
                centerHeight + shapeYOffset,
                DEFAULT_TEXT_COLOR
        );

        pose.popPose();
    }

    public static EventResult onMouseScroll(Minecraft minecraft, double x, double y) {
        if (LiteminerClient.isVeinMining()) {
            if (y != 0) {
                if (y > 0) {
                    LiteminerClient.shapes.previousItem();
                } else if (y < 0) {
                    LiteminerClient.shapes.nextItem();
                }
                LiteminerClient.sendStateToServer(LiteminerClient.isVeinMining());
            }
            if (!LiteminerClient.CONFIG.showHUD.get()) {
                assert minecraft.player != null;
                FeedbackHelper.actionBarMessage(minecraft.player, Component.translatable(
                        "hud.liteminer.changed_shape",
                        LiteminerClient.shapes.getCurrentItem().getDisplayName()
                ));
            }
            return EventResult.interruptFalse();
        }

        return EventResult.pass();
    }
}
