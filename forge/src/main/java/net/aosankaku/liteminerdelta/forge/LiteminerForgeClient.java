package net.aosankaku.liteminerdelta.forge;

import net.aosankaku.liteminerdelta.Liteminer;
import net.aosankaku.liteminerdelta.LiteminerClient;
import net.aosankaku.liteminerdelta.rendering.BlockHighlightRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

public class LiteminerForgeClient {
    @Mod.EventBusSubscriber(modid = Liteminer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value =
            Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void onConstructMod(final FMLConstructModEvent evt) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, LiteminerClient.CONFIG_SPEC);
            if (ModList.get().isLoaded("cloth_config")) {
                ClothConfigIntegration.registerConfigScreen();
            }
            LiteminerClient.init();
        }
    }

    @Mod.EventBusSubscriber(modid = Liteminer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value =
            Dist.CLIENT)
    public static class ForgeBus {
        @SubscribeEvent
        public static void renderBlockHighlights(RenderHighlightEvent.Block event) {
            event.setCanceled(!BlockHighlightRenderer.renderLiteminerHighlight(event.getPoseStack()));
        }
    }
}

