pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
        maven("https://papermc.io/repo/repository/maven-public/")
        google()
    }

    val idofrontVersion: String by settings
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("com.mineinabyss.conventions"))
                useVersion(idofrontVersion)
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

dependencyResolutionManagement {
    val idofrontVersion: String by settings

    repositories {
        maven("https://repo.mineinabyss.com/releases")
    }

    versionCatalogs {
        create("libs").from("com.mineinabyss:catalog:$idofrontVersion")
        create("myLibs").from(files("gradle/myLibs.versions.toml"))
    }
}
