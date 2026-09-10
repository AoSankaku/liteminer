import com.iamkaf.multiloader.support.MultiloaderProjectContext

plugins {
    id("com.iamkaf.multiloader.forge")
}

apply(from = rootProject.file("gradle/distribution.gradle.kts"))

apply(from = rootProject.file("gradle/amber-delta.gradle.kts"))

val multiloader = MultiloaderProjectContext.of(project)
val minecraftVersion = multiloader.minecraftVersion()
val catalog = multiloader.catalogFor()

dependencies {
    if (minecraftVersion != "26.2") {
        add("implementation", multiloader.library(catalog, "forgeconfigapiport-forge"))
    }
}
