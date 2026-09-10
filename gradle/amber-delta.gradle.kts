val amberDeltaModules = setOf("amber-common", "amber-fabric", "amber-forge", "amber-neoforge")

repositories {
    mavenLocal()
}

configurations.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "com.iamkaf.amber"
            && requested.name in amberDeltaModules
            && requested.version != null
        ) {
            useTarget("net.aosankaku.amberdelta:${requested.name}:${requested.version}-delta.1")
            because("Liteminer Delta requires Amber Delta's optional networking API")
        }
    }
}
