import org.gradle.api.file.DuplicatesStrategy
import org.gradle.jvm.tasks.Jar

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(rootProject.file("LICENSE")) {
        into("META-INF")
        rename { "LICENSE" }
    }
}
