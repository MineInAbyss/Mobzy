rootProject.name = "mobzy"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
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

dependencyResolutionManagement {
    val idofrontVersion: String by settings

    repositories {
        maven("https://repo.mineinabyss.com/releases")
    }

    versionCatalogs {
        create("libs") {
            from("com.mineinabyss:catalog:$idofrontVersion")
            version("modelengine", "R4.0.4")
        }
        create("myLibs").from(files("gradle/myLibs.versions.toml"))
    }
}

include(
    "mobzy-core",
    "mobzy-features",
    "mobzy-modelengine",
    "mobzy-pathfinding",
    "mobzy-spawning",
)
