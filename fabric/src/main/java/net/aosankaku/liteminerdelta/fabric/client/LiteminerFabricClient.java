package net.aosankaku.liteminerdelta.fabric.client;

import com.iamkaf.konfig.api.v1.KonfigClientScreens;
import net.aosankaku.liteminerdelta.Constants;
import net.aosankaku.liteminerdelta.LiteminerClient;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;

public final class LiteminerFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LiteminerClient.init();
        LiteminerClient.setOpenConfigScreenCallback(
                () -> Minecraft.getInstance().gui.setScreen(KonfigClientScreens.create(Constants.MOD_ID, Minecraft.getInstance().gui.screen())));
    }
}
