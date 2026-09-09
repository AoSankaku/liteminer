package net.aosankaku.liteminerdelta.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class LiteminerClientConfig {
    public final ModConfigSpec.ConfigValue<KeyMode> keyMode;
    public final ModConfigSpec.ConfigValue<Boolean> autoVeinMineOres;
    public final ModConfigSpec.ConfigValue<Boolean> showHUD;
    public final ModConfigSpec.ConfigValue<Double> hud_scale;
    public final ModConfigSpec.ConfigValue<Boolean> distinguishDeepslateOres;
    public final ModConfigSpec.ConfigValue<Boolean> distinguishStoneVariants;

    public final ModConfigSpec.ConfigValue<Boolean> showHighlights;
    public final ModConfigSpec.ConfigValue<LineColor> highlightForegroundLineColor;
    public final ModConfigSpec.ConfigValue<LineColor> highlightSeeThroughLineColor;

    public LiteminerClientConfig(ModConfigSpec.Builder builder) {
        keyMode = builder.translation("liteminer_delta.config.key_mode")
                .comment(":)")
                .defineEnum("key_mode", KeyMode.HOLD);
        autoVeinMineOres = builder.translation("liteminer_delta.config.auto_veinmine_ores")
                .comment("Automatically activates vein mining while targeting an ore.")
                .define("auto_veinmine_ores", false);
        showHUD = builder.translation("liteminer_delta.config.show_hud").comment(":)").define("show_hud", true);
        hud_scale = builder.translation("liteminer_delta.config.hud_scale")
                .comment(":)")
                .defineInRange("hud_scale", 1d, 0.5d, 2d);
        distinguishDeepslateOres = builder.translation("liteminer_delta.config.distinguish_deepslate_ores")
                .comment("Whether regular and deepslate ore variants should be mined separately.")
                .define("distinguish_deepslate_ores", true);
        distinguishStoneVariants = builder.translation("liteminer_delta.config.distinguish_stone_variants")
                .comment("Whether blocks in the same base stone tag should be mined separately.")
                .define("distinguish_stone_variants", true);

        showHighlights = builder.translation("liteminer_delta.config.show_highlights")
                .comment("Show block highlights when veinmining")
                .define("show_highlights", true);

        highlightForegroundLineColor = builder.translation("liteminer_delta.config.highlight_foreground_line_color")
                .comment(":)")
                .defineEnum("highlight_foreground_line_color", LineColor.WHITE);

        highlightSeeThroughLineColor = builder.translation("liteminer_delta.config.highlight_see_through_line_color")
                .comment(":)")
                .defineEnum("highlight_see_through_line_color", LineColor.CYAN);
    }
}
