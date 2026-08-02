package net.aosankaku.liteminerdelta.fabric;

import net.fabricmc.api.ModInitializer;

import net.aosankaku.liteminerdelta.LiteminerMod;

public final class LiteminerFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LiteminerMod.init();
    }
}
