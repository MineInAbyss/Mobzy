pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
        google()
        mavenLocal()
    }

    plugins {
        val kotlinVersion: String by settings
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        kotlin("kapt") version kotlinVersion
    }

    val miaConventionsVersion: String by settings
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("com.mineinabyss.conventions"))
                useVersion(miaConventionsVersion)
        }
    }
}

rootProject.name = "mobzy"

includeBuild("../Geary")
includeBuild("../Idofront")
