package net.aosankaku.liteminerdelta.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class LiteminerConfig {
    public final ModConfigSpec.ConfigValue<Boolean> requireCorrectToolEnabled;
    public final ModConfigSpec.ConfigValue<Boolean> preventToolBreaking;
    public final ModConfigSpec.ConfigValue<Integer> blockBreakLimit;
    public final ModConfigSpec.ConfigValue<Boolean> harvestTimePerBlockModifierEnabled;
    public final ModConfigSpec.ConfigValue<Double> harvestTimePerBlockModifier;
    public final ModConfigSpec.ConfigValue<Boolean> foodExhaustionEnabled;
    public final ModConfigSpec.ConfigValue<Double> foodExhaustion;
    public final ModConfigSpec.ConfigValue<Boolean> allowVeinMiningAtZeroHunger;
    public final ModConfigSpec.ConfigValue<Boolean> distinguishGrownCrops;

    public LiteminerConfig(ModConfigSpec.Builder builder) {
        preventToolBreaking = builder.translation("liteminer_delta.config.prevent_tool_breaking")
                .comment(":)")
                .define("prevent_tool_breaking", true);
        requireCorrectToolEnabled = builder.translation("liteminer_delta.config.require_correct_tool_enabled")
                .comment(":)")
                .define("require_correct_tool_enabled", false);

        blockBreakLimit = builder.translation("liteminer_delta.config.block_break_limit")
                .comment(":)")
                .defineInRange("block_break_limit", 64, 1, 2048);

        harvestTimePerBlockModifierEnabled =
                builder.translation("liteminer_delta.config.harvest_time_per_block_modifier_enabled")
                        .comment(":)")
                        .define("harvest_time_per_block_modifier_enabled", true);
        harvestTimePerBlockModifier = builder.translation("liteminer_delta.config.harvest_time_per_block_modifier")
                .comment(":)")
                .defineInRange("harvest_time_per_block_modifier", 2d, 1.0d, 10d);

        foodExhaustionEnabled = builder.translation("liteminer_delta.config.food_exhaustion_enabled")
                .comment(":)")
                .define("food_exhaustion_enabled", true);
        foodExhaustion = builder.translation("liteminer_delta.config.food_exhaustion")
                .comment(":)")
                .defineInRange("food_exhaustion", 0.2d, 0.0d, 1d);
        allowVeinMiningAtZeroHunger =
                builder.translation("liteminer_delta.config.allow_vein_mining_at_zero_hunger")
                        .comment("Allows vein mining when the player's food level is zero.")
                        .define("allow_vein_mining_at_zero_hunger", false);

        distinguishGrownCrops = builder.translation("liteminer_delta.config.distinguish_grown_crops")
                .comment(":)")
                .define("distinguish_grown_crops", true);

    }
}
