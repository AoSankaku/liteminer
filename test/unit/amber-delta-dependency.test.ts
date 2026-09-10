import { describe, expect, test } from "bun:test";
import { fileURLToPath } from "node:url";

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

    test("publishes against the Amber Delta Modrinth project", async () => {
        const files = [...new Bun.Glob("versions/*/gradle.properties").scanSync({ cwd: fileURLToPath(root) })].sort();

        expect(files).toHaveLength(5);
        for (const file of files) {
            const properties = await source(file);
            expect(properties, file).toContain("dependencies.modrinth.required=amber-delta,");
        }
    });

    test("uses the configured Modrinth project and stages only Delta release jars", async () => {
        const properties = await source("gradle.properties");
        const distribution = await source("gradle/distribution.gradle.kts");
        const justfile = await source("justfile");

        expect(properties).toContain("publish.dry-run=false");
        expect(properties).toContain("publish.modrinth.id=liteminer-delta");
        expect(distribution).toContain("withType<Jar>()");
        expect(distribution).toContain('rootProject.file("LICENSE")');
        expect(distribution).toContain('into("META-INF")');
        expect(distribution).toContain('rename("LICENSE")');
        expect(justfile).toContain("stage-modrinth version:");
        expect(justfile).toContain('liteminer_delta-${loader}-*-delta.1+{{version}}.jar');
        for (const loader of ["common", "fabric", "forge", "neoforge"]) {
            const buildScript = await source(`${loader}/build.gradle.kts`);
            expect(buildScript, loader).toContain('apply(from = rootProject.file("gradle/distribution.gradle.kts"))');
        }
    });
});
