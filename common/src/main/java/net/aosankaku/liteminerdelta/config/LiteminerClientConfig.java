package net.aosankaku.liteminerdelta.config;

import com.iamkaf.konfig.api.v1.ConfigBuilder;
import com.iamkaf.konfig.api.v1.ConfigValue;

public final class LiteminerClientConfig {
    public final ConfigValue<String> keyMode;
    public final ConfigValue<Boolean> autoVeinMineOres;
    public final ConfigValue<Boolean> showHUD;
    public final ConfigValue<Double> hud_scale;
    public final ConfigValue<Boolean> distinguishDeepslateOres;
    public final ConfigValue<Boolean> distinguishStoneVariants;

    public final ConfigValue<Boolean> showHighlights;
    public final ConfigValue<Integer> highlightForegroundLineColor;
    public final ConfigValue<Integer> highlightSeeThroughLineColor;

    public LiteminerClientConfig(ConfigBuilder builder) {
        builder.push("controls")
                .categoryComment("Local input behavior for activating vein mining.")
                .categoryInfo(info -> info
                        .headerKey("liteminer_delta.config.info.controls.header")
                        .inlineTextKey("liteminer_delta.config.info.controls.text"))
                .header("Controls");
        keyMode = builder.dropdown("key_mode", KeyMode.HOLD.name(), options -> options
                        .option(KeyMode.HOLD.name(), "Hold", option -> option.tooltip("Hold the vein mining key to stay active."))
                        .option(KeyMode.TOGGLE.name(), "Toggle", option -> option.tooltip("Press the vein mining key to switch on or off.")))
                .comment("Controls how the vein mining keybind activates vein mining.")
                .info(info -> info.inlineTextKey("liteminer_delta.config.key_mode.info"))
                .clientOnly()
                .build();
        autoVeinMineOres = builder.bool("auto_veinmine_ores", false)
                .comment("Automatically activates vein mining while targeting an ore.")
                .info(info -> info.inlineTextKey("liteminer_delta.config.auto_veinmine_ores.info"))
                .clientOnly()
                .build();
        builder.pop();

        builder.push("hud")
                .categoryComment("Client HUD text shown while vein mining.")
                .categoryInfo(info -> info
                        .headerKey("liteminer_delta.config.info.hud.header")
                        .inlineTextKey("liteminer_delta.config.info.hud.text"))
                .header("HUD");
        showHUD = builder.bool("show_hud", true)
                .comment("Shows selected block count and current vein mining shape on the HUD.")
                .info(info -> info.inlineTextKey("liteminer_delta.config.show_hud.info"))
                .clientOnly()
                .build();
        hud_scale = builder.doubleRange("hud_scale", 1d, 0.5d, 2d)
                .comment("Scales Liteminer HUD text.")
                .info(info -> info.inlineTextKey("liteminer_delta.config.hud_scale.info"))
                .clientOnly()
                .build();
        builder.pop();

        builder.push("matching")
                .categoryComment("Client-side block matching preferences.")
                .categoryInfo(info -> info
                        .headerKey("liteminer_delta.config.info.matching.header")
                        .inlineTextKey("liteminer_delta.config.info.matching.text"))
                .header("Block Matching");
        distinguishDeepslateOres = builder.bool("distinguish_deepslate_ores", true)
                .comment("Keeps regular and deepslate ore variants in separate vein mining groups.")
                .info(info -> info.inlineTextKey("liteminer_delta.config.distinguish_deepslate_ores.info"))
                .clientOnly()
                .build();
        distinguishStoneVariants = builder.bool("distinguish_stone_variants", true)
                .comment("Keeps blocks in the same base stone tag in separate vein mining groups.")
                .info(info -> info.inlineTextKey("liteminer_delta.config.distinguish_stone_variants.info"))
                .clientOnly()
                .build();
        builder.pop();

        builder.push("highlights")
                .categoryComment("Client block outline rendering for selected vein mining blocks.")
                .categoryInfo(info -> info
                        .headerKey("liteminer_delta.config.info.highlights.header")
                        .inlineTextKey("liteminer_delta.config.info.highlights.text"))
                .header("Highlights");
        showHighlights = builder.bool("show_highlights", true)
                .comment("Shows block highlights when vein mining.")
                .info(info -> info.inlineTextKey("liteminer_delta.config.show_highlights.info"))
                .clientOnly()
                .build();

        highlightForegroundLineColor = builder.colorRgb("highlight_foreground_line_color", 0xF9FFFE)
                .comment("Color used for normal visible highlight lines.")
                .info(info -> info.inlineTextKey("liteminer_delta.config.highlight_foreground_line_color.info"))
                .clientOnly()
                .build();

        highlightSeeThroughLineColor = builder.colorRgb("highlight_see_through_line_color", 0x169C9C)
                .comment("Color used for see-through highlight lines.")
                .info(info -> info.inlineTextKey("liteminer_delta.config.highlight_see_through_line_color.info"))
                .clientOnly()
                .build();
        builder.pop();
    }

    public KeyMode keyMode() {
        return KeyMode.valueOf(keyMode.get());
    }
}
