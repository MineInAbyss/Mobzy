import Com_mineinabyss_conventions_platform_gradle.Deps

val gearyVersion: String by project
val guiyVersion: String by project

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose") version "1.0.1"
}

repositories {
    maven("https://mvn.lumine.io/repository/maven-public/") // Model Engine
    maven("https://repo.dmulloy2.net/nexus/repository/public/") //ProtocolLib
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(Deps.kotlinx.serialization.json)
    compileOnly(Deps.kotlinx.serialization.kaml)
    compileOnly(Deps.kotlinx.coroutines)
    compileOnly(Deps.minecraft.skedule)

    compileOnly("com.mineinabyss:geary-papermc-core:$gearyVersion")
    compileOnly("com.mineinabyss:guiy-compose:$guiyVersion")
    compileOnly("com.ticxo.modelengine:api:R2.3.1")
    compileOnly("com.mineinabyss:protocolburrito:0.2.25")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")

    compileOnly(project(":mobzy-core"))
    compileOnly(project(":mobzy-components"))
    compileOnly(project(":mobzy-nms-injection"))
}
