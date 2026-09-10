plugins {
    id("com.iamkaf.multiloader.neoforge")
}

apply(from = rootProject.file("gradle/distribution.gradle.kts"))

apply(from = rootProject.file("gradle/amber-delta.gradle.kts"))
