package com.iamkaf.liteminer.event;

import com.iamkaf.liteminer.Liteminer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

final class FoodExhaustion {
    private static final ResourceLocation FARMERS_DELIGHT_NOURISHMENT =
            new ResourceLocation("farmersdelight", "nourishment");

    private FoodExhaustion() {
    }

    static boolean canUseLiteminer(Player player) {
        return player.isCreative()
                || Liteminer.CONFIG.allowVeinMiningAtZeroHunger.get()
                || !isEnabled()
                || player.getFoodData().getFoodLevel() > 0;
    }

    static void apply(Player player) {
        if (!isEnabled() || hasFarmersDelightNourishment(player)) {
            return;
        }

        player.causeFoodExhaustion(getExhaustion());
    }

    private static boolean isEnabled() {
        return Liteminer.CONFIG.foodExhaustionEnabled.get() && getExhaustion() > 0;
    }

    private static float getExhaustion() {
        return Liteminer.CONFIG.foodExhaustion.get().floatValue();
    }

    private static boolean hasFarmersDelightNourishment(Player player) {
        return BuiltInRegistries.MOB_EFFECT.getOptional(FARMERS_DELIGHT_NOURISHMENT)
                .map(player::hasEffect)
                .orElse(false);
    }
}
