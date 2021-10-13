import Com_mineinabyss_conventions_platform_gradle.Deps

val gearyVersion: String by project

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    kotlin("plugin.serialization")
}

dependencies {
    compileOnly(Deps.kotlinx.serialization.json)

    compileOnly("com.mineinabyss:geary-platform-papermc:$gearyVersion")

    compileOnly(project(":mobzy-core"))
    compileOnly(project(":mobzy-components"))
}
