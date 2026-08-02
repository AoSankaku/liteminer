package net.aosankaku.liteminerdelta.forge;

import net.aosankaku.liteminerdelta.Constants;
import net.aosankaku.liteminerdelta.Liteminer;
import net.aosankaku.liteminerdelta.LiteminerClient;
import net.aosankaku.liteminerdelta.LiteminerMod;
import com.iamkaf.amber.api.platform.v1.Platform;
import fuzs.forgeconfigapiport.forge.api.v5.NeoForgeConfigRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Constants.MOD_ID)
public class LiteminerForge {

    public LiteminerForge() {
        NeoForgeConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.COMMON, Liteminer.CONFIG_SPEC);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForgeConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.CLIENT, LiteminerClient.CONFIG_SPEC);
            LiteminerClient.setOpenConfigScreenCallback(
                    () -> LiteminerClient.showConfigScreenUnavailableMessage(Platform.getConfigFolder()));
            LiteminerClient.init();
        }
        LiteminerMod.init();
    }
}
