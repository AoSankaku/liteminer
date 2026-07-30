package com.iamkaf.liteminer.event;

import com.iamkaf.liteminer.Liteminer;
import net.minecraft.world.entity.player.Player;

final class FoodExhaustion {
    private FoodExhaustion() {
    }

    static boolean canUseLiteminer(Player player) {
        return player.isCreative()
                || Liteminer.CONFIG.allowVeinMiningAtZeroHunger.get()
                || !isEnabled()
                || player.getFoodData().getFoodLevel() > 0;
    }

    private static boolean isEnabled() {
        return Liteminer.CONFIG.foodExhaustionEnabled.get() && getExhaustion() > 0;
    }

    private static float getExhaustion() {
        return Liteminer.CONFIG.foodExhaustion.get().floatValue();
    }
}
