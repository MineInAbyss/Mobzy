plugins {
    id("com.mineinabyss.conventions.kotlin.jvm")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.publication")
    kotlin("plugin.serialization")
}

dependencies {
    compileOnly(libs.kotlinx.serialization.json)

    // As a rule, core must not depend on any other modules
    // Components are an exception since they should only contain data
    compileOnly(project(":mobzy-components"))
}
