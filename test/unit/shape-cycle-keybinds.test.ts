import { describe, expect, test } from "bun:test";

const root = new URL("../../", import.meta.url);

const overlays = [
    "common",
    "versions/1.21.11/common",
    "versions/26.1/common",
    "versions/26.1.1/common",
    "versions/26.1.2/common",
];

async function source(relativePath: string): Promise<string> {
    return Bun.file(new URL(relativePath, root)).text();
}

describe("shape cycle keybind fallback", () => {
    test("every supported client registers unbound previous and next shape keybinds", async () => {
        for (const overlay of overlays) {
            const client = await source(
                `${overlay}/src/main/java/net/aosankaku/liteminerdelta/LiteminerClient.java`,
            );

            expect(client).toMatch(
                /PREVIOUS_SHAPE_KEY_MAPPING\s*=\s*new KeyMapping\("key\.liteminer_delta\.previous_shape",\s*InputConstants\.Type\.KEYSYM,\s*InputConstants\.UNKNOWN\.getValue\(\),\s*KEY_CATEGORY\)/s,
            );
            expect(client).toMatch(
                /NEXT_SHAPE_KEY_MAPPING\s*=\s*new KeyMapping\("key\.liteminer_delta\.next_shape",\s*InputConstants\.Type\.KEYSYM,\s*InputConstants\.UNKNOWN\.getValue\(\),\s*KEY_CATEGORY\)/s,
            );
            expect(client).toContain("KeybindHelper.register(PREVIOUS_SHAPE_KEY_MAPPING);");
            expect(client).toContain("KeybindHelper.register(NEXT_SHAPE_KEY_MAPPING);");
            expect(client).toContain("if (PREVIOUS_SHAPE_KEY_MAPPING.consumeClick())");
            expect(client).toContain("if (NEXT_SHAPE_KEY_MAPPING.consumeClick())");
        }
    });

    test("keybinds and mouse wheel share the active-mode shape cycling behavior", async () => {
        for (const overlay of overlays) {
            const client = await source(
                `${overlay}/src/main/java/net/aosankaku/liteminerdelta/LiteminerClient.java`,
            );
            const hud = await source(
                `${overlay}/src/main/java/net/aosankaku/liteminerdelta/rendering/HUD.java`,
            );

            expect(client).toMatch(
                /public static boolean cycleShape\(boolean previous\) \{\s*if \(!isVeinMining\(\)\) \{\s*return false;\s*}\s*if \(previous\) \{\s*shapes\.previousItem\(\);\s*} else \{\s*shapes\.nextItem\(\);\s*}\s*sendStateToServer\(true\);\s*return true;\s*}/s,
            );
            expect(hud).toContain("public static InteractionResult onMouseScroll");
            expect(hud).toContain("LiteminerClient.cycleShape(scrollY > 0)");
            expect(client).toContain("InputEvents.MOUSE_SCROLL_PRE.register(HUD::onMouseScroll);");
        }
    });

    test("control labels advertise the existing mouse wheel controls", async () => {
        const english = JSON.parse(
            await source("common/src/main/resources/assets/liteminer_delta/lang/en_us.json"),
        );
        const japanese = JSON.parse(
            await source("common/src/main/resources/assets/liteminer_delta/lang/ja_jp.json"),
        );

        expect(english["key.liteminer_delta.previous_shape"]).toContain("Mouse Wheel Up");
        expect(english["key.liteminer_delta.next_shape"]).toContain("Mouse Wheel Down");
        expect(japanese["key.liteminer_delta.previous_shape"]).toContain("マウスホイール上");
        expect(japanese["key.liteminer_delta.next_shape"]).toContain("マウスホイール下");
    });
});
