import Com_mineinabyss_conventions_platform_gradle.Deps

val gearyVersion: String by project

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.nms")
    kotlin("plugin.serialization")
}

dependencies {
    compileOnly(libs.kotlinx.serialization.json)

    compileOnly("com.mineinabyss:geary-papermc-core:$gearyVersion")

    compileOnly(project(":mobzy-core"))
    compileOnly(project(":mobzy-components"))
}
