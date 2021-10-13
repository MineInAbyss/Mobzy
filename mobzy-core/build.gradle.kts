import Com_mineinabyss_conventions_platform_gradle.Deps

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    kotlin("plugin.serialization")
}

dependencies {
    compileOnly(Deps.kotlinx.serialization.json)

    // As a rule, core must not depend on any other modules
    // Components are an exception since they should only contain data
    compileOnly(project(":mobzy-components"))
}
