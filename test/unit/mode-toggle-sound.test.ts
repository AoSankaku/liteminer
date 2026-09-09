import { describe, expect, test } from "bun:test";

const root = new URL("../../", import.meta.url);

async function source(relativePath: string): Promise<string> {
    return Bun.file(new URL(relativePath, root)).text();
}

describe("mode toggle sound", () => {
    test("every supported source overlay plays distinct sounds after an accepted keybind state change", async () => {
        const clients = [
            "common/src/main/java/net/aosankaku/liteminerdelta/LiteminerClient.java",
            "versions/1.21.11/common/src/main/java/net/aosankaku/liteminerdelta/LiteminerClient.java",
            "versions/26.1/common/src/main/java/net/aosankaku/liteminerdelta/LiteminerClient.java",
            "versions/26.1.1/common/src/main/java/net/aosankaku/liteminerdelta/LiteminerClient.java",
            "versions/26.1.2/common/src/main/java/net/aosankaku/liteminerdelta/LiteminerClient.java",
        ];

        for (const clientPath of clients) {
            const client = await source(clientPath);

            expect(client).toContain("SoundEvents.UI_BUTTON_CLICK");
            expect(client).toContain("enabled ? 1.2F : 0.8F");
            expect(client).toContain("boolean previousKeybindState = keybindState;");
            expect(client).toContain("boolean keybindStateChanged = keybindState != previousKeybindState;");
            expect(client).toMatch(
                /if \(newState == isVeinMining\(\)\) \{\s*return;\s*}\s*sendStateToServer\(newState\);\s*currentState = newState;\s*if \(keybindStateChanged\) \{\s*playModeToggleSound\(newState\);\s*}/s,
            );
        }
    });
});
