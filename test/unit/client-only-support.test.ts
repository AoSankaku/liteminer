import { describe, expect, test } from "bun:test";

const root = new URL("../../", import.meta.url);

async function source(relativePath: string): Promise<string> {
    return Bun.file(new URL(relativePath, root)).text();
}

describe("client-only server support", () => {
    test("the Liteminer Delta channel is optional and exposes server support", async () => {
        const networking = await source("common/src/main/java/net/aosankaku/liteminerdelta/networking/LiteminerNetwork.java");

        expect(networking).toContain("NetworkChannel.createOptional(");
        expect(networking).toContain("isServerSupported()");
        expect(networking).toContain("NET.canSendToServer(C2SVeinmineKeybindChange.class)");
    });

    test("every supported source overlay gates activation before updating client state", async () => {
        const clients = [
            "common/src/main/java/net/aosankaku/liteminerdelta/LiteminerClient.java",
            "versions/1.21.11/common/src/main/java/net/aosankaku/liteminerdelta/LiteminerClient.java",
            "versions/26.1/common/src/main/java/net/aosankaku/liteminerdelta/LiteminerClient.java",
            "versions/26.1.1/common/src/main/java/net/aosankaku/liteminerdelta/LiteminerClient.java",
            "versions/26.1.2/common/src/main/java/net/aosankaku/liteminerdelta/LiteminerClient.java",
        ];

        for (const clientPath of clients) {
            const client = await source(clientPath);
            expect(client).toContain("ACTIVATION_GATE.evaluate(");
            expect(client).toContain("LiteminerNetwork.isServerSupported()");
            expect(client).toContain('Component.translatable("message.liteminer_delta.server_unavailable")');
        }
    });

    test("metadata permits Liteminer Delta to be absent from either side", async () => {
        const properties = await source("gradle.properties");
        const language = await source("common/src/main/resources/assets/liteminer_delta/lang/en_us.json");
        const forgeMetadataFiles = [
            "forge/src/main/resources/META-INF/mods.toml",
            "versions/1.21.11/forge/src/main/resources/META-INF/mods.toml",
            "versions/26.1/forge/src/main/resources/META-INF/mods.toml",
            "versions/26.1.1/forge/src/main/resources/META-INF/mods.toml",
            "versions/26.1.2/forge/src/main/resources/META-INF/mods.toml",
        ];

        expect(properties).toContain("environments.client=optional");
        expect(properties).toContain("environments.server=optional");
        expect(language).toContain('"message.liteminer_delta.server_unavailable"');
        for (const metadataPath of forgeMetadataFiles) {
            const forgeMetadata = await source(metadataPath);
            expect(forgeMetadata).toContain('displayTest = "IGNORE_ALL_VERSION"');
        }
    });

    test("server-side key state is discarded when a player disconnects", async () => {
        const events = await source("common/src/main/java/net/aosankaku/liteminerdelta/event/Events.java");
        const implementations = [
            "common/src/main/java/net/aosankaku/liteminerdelta/Liteminer.java",
            "versions/1.21.11/common/src/main/java/net/aosankaku/liteminerdelta/Liteminer.java",
            "versions/26.1/common/src/main/java/net/aosankaku/liteminerdelta/Liteminer.java",
            "versions/26.1.1/common/src/main/java/net/aosankaku/liteminerdelta/Liteminer.java",
            "versions/26.1.2/common/src/main/java/net/aosankaku/liteminerdelta/Liteminer.java",
        ];

        expect(events).toContain("PlayerEvents.PLAYER_LEAVE.register");
        for (const implementationPath of implementations) {
            const implementation = await source(implementationPath);
            expect(implementation).toContain("playerStateMap.remove(player.getUUID())");
        }
    });

    test("Minecraft 26.2 uses the current overlay message API", async () => {
        const client = await source("common/src/main/java/net/aosankaku/liteminerdelta/LiteminerClient.java");

        expect(client).toContain("minecraft.player.sendOverlayMessage(");
    });

    test("Minecraft 26.1 releases use the current overlay message API", async () => {
        for (const version of ["26.1", "26.1.1", "26.1.2"]) {
            const client = await source(
                `versions/${version}/common/src/main/java/net/aosankaku/liteminerdelta/LiteminerClient.java`,
            );
            expect(client, version).toContain("minecraft.player.sendOverlayMessage(");
            expect(client, version).not.toContain("minecraft.player.displayClientMessage(");
        }
    });
});
