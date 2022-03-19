pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
        maven("https://papermc.io/repo/repository/maven-public/")
        google()
        mavenLocal()
    }

    plugins {
        val kotlinVersion: String by settings
        kotlin("plugin.serialization") version kotlinVersion
    }

    val idofrontConventions: String by settings
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("com.mineinabyss.conventions"))
                useVersion(idofrontConventions)
        }
    }
}

rootProject.name = "mobzy"

include(
    "mobzy-pathfinding",
    "mobzy-systems",
    "mobzy-spawning",
    "mobzy-nms-injection",
    "mobzy-core",
    "mobzy-components"
)
