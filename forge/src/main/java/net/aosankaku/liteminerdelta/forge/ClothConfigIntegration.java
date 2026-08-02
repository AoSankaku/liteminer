package net.aosankaku.liteminerdelta.forge;

import net.aosankaku.liteminerdelta.LiteminerClient;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

final class ClothConfigIntegration {
    private ClothConfigIntegration() {
    }

    static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(ClothConfigIntegration::createScreen)
        );
    }

    private static Screen createScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("liteminer_delta.configuration.title"));
        ConfigCategory category = builder.getOrCreateCategory(
                Component.translatable("liteminer_delta.configuration.title")
        );
        ConfigEntryBuilder entries = builder.entryBuilder();

        category.addEntry(entries.startBooleanToggle(
                        Component.translatable("liteminer_delta.config.distinguish_deepslate_ores"),
                        LiteminerClient.CONFIG.distinguishDeepslateOres.get()
                )
                .setDefaultValue(true)
                .setTooltip(Component.translatable(
                        "liteminer_delta.config.distinguish_deepslate_ores.tooltip"
                ))
                .setSaveConsumer(LiteminerClient.CONFIG.distinguishDeepslateOres::set)
                .build());

        builder.setSavingRunnable(() -> {
            LiteminerClient.CONFIG_SPEC.save();
            LiteminerClient.syncStateToServer();
        });
        return builder.build();
    }
}
