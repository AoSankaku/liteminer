import com.iamkaf.multiloader.support.MultiloaderProjectContext

plugins {
    id("com.iamkaf.multiloader.fabric")
}

apply(from = rootProject.file("gradle/amber-delta.gradle.kts"))

val multiloader = MultiloaderProjectContext.of(project)
val minecraftVersion = multiloader.minecraftVersion()
val catalog = multiloader.catalogFor()

dependencies {
    if (minecraftVersion != "26.2") {
        val configuration = if (multiloader.useUnobfuscatedMinecraft()) "implementation" else "modImplementation"
        add(configuration, multiloader.library(catalog, "forgeconfigapiport-fabric"))
    }
}
