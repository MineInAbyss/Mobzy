import Com_mineinabyss_conventions_platform_gradle.Deps

val gearyVersion: String by project

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    kotlin("plugin.serialization")
}

repositories {
    maven("https://mvn.lumine.io/repository/maven-public/") // Model Engine
}

dependencies {
    compileOnly(Deps.kotlinx.serialization.json)

    compileOnly("com.mineinabyss:geary-papermc-core:$gearyVersion")
    compileOnly("com.ticxo.modelengine:api:R2.2.0")
}
