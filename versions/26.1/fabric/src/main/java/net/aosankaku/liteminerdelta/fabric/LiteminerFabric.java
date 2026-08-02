package net.aosankaku.liteminerdelta.fabric;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;

import net.aosankaku.liteminerdelta.Constants;
import net.aosankaku.liteminerdelta.Liteminer;
import net.aosankaku.liteminerdelta.LiteminerMod;
import net.neoforged.fml.config.ModConfig;

public final class LiteminerFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LiteminerMod.init();
        ConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.COMMON, Liteminer.CONFIG_SPEC);
    }
}
