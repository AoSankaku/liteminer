package com.iamkaf.liteminer.config;


import net.minecraftforge.common.ForgeConfigSpec;

public final class LiteminerClientConfig {
    public final ForgeConfigSpec.ConfigValue<KeyMode> keyMode;
    public final ForgeConfigSpec.ConfigValue<Boolean> showHUD;
    public final ForgeConfigSpec.ConfigValue<Double> hud_scale;
    public final ForgeConfigSpec.ConfigValue<Boolean> distinguishDeepslateOres;

    public LiteminerClientConfig(ForgeConfigSpec.Builder builder) {
        keyMode = builder.translation("liteminer.config.key_mode")
                .comment(":)")
                .defineEnum("key_mode", KeyMode.HOLD);
        showHUD = builder.translation("liteminer.config.show_hud").comment(":)").define("show_hud", true);
        hud_scale = builder.translation("liteminer.config.hud_scale")
                .comment(":)")
                .defineInRange("hud_scale", 1d, 0.5d, 2d);
        distinguishDeepslateOres = builder.translation("liteminer.config.distinguish_deepslate_ores")
                .comment("Whether regular and deepslate ore variants should be mined separately.")
                .define("distinguish_deepslate_ores", true);
    }
}
