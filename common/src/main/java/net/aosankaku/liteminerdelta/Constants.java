package net.aosankaku.liteminerdelta;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Constants {
    public static final String MOD_ID = "liteminer_delta";
    public static final String MOD_NAME = "Liteminer Delta";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    private Constants() {
    }

    public static Identifier resource(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
