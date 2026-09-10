import com.iamkaf.multiloader.publishing.MultiloaderPublishingExtension

plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") apply false
    id("net.fabricmc.fabric-loom") apply false
    id("com.iamkaf.multiloader.root")
}

stonecutter active "26.1.2".let { multiloaderStonecutter.active(it) }

extensions.configure<MultiloaderPublishingExtension>("multiloaderPublishing") {
    publish {
        modrinth {
            dependencies {
                getRequired().set(
                    providers.gradleProperty("publish.modrinth.dependencies")
                        .orElse("amber-delta")
                        .map { it.split(',').map(String::trim) }
                )
            }
        }
    }
}
