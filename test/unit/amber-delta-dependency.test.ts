import { describe, expect, test } from "bun:test";

const root = new URL("../../", import.meta.url);

async function source(relativePath: string): Promise<string> {
    return Bun.file(new URL(relativePath, root)).text();
}

describe("Amber Delta build dependency", () => {
    test("replaces every official Amber module with the AoSankaku fork coordinate", async () => {
        const dependencyRules = await source("gradle/amber-delta.gradle.kts");

        expect(dependencyRules).toContain("net.aosankaku.amberdelta");
        expect(dependencyRules).toContain("com.iamkaf.amber");
        expect(dependencyRules).toContain("-delta.1");
        for (const loader of ["common", "fabric", "forge", "neoforge"]) {
            expect(dependencyRules).toContain(`amber-${loader}`);
        }
    });

    test("applies the replacement rule in every loader project", async () => {
        for (const loader of ["common", "fabric", "forge", "neoforge"]) {
            const buildScript = await source(`${loader}/build.gradle.kts`);
            expect(buildScript, loader).toContain('apply(from = rootProject.file("gradle/amber-delta.gradle.kts"))');
        }
    });

    test("resolves unpublished Amber Delta artifacts from the local Maven repository", async () => {
        const dependencyRules = await source("gradle/amber-delta.gradle.kts");

        expect(dependencyRules).toContain("repositories");
        expect(dependencyRules).toContain("mavenLocal()");
    });
});
