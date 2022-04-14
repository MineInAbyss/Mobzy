val gearyVersion: String by project

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.nms")
    kotlin("plugin.serialization")
}

repositories {
    maven("https://repo.dmulloy2.net/nexus/repository/public/") //ProtocolLib
    maven("https://mvn.lumine.io/repository/maven-public/") // Model Engine
    maven("https://jitpack.io")
    mavenLocal()
}

dependencies {
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    compileOnly(libs.kotlinx.coroutines)
    compileOnly(libs.minecraft.skedule)

    compileOnly("com.mineinabyss:geary-papermc-core:$gearyVersion")
    compileOnly("com.ticxo.modelengine:api:R2.5.0")
    compileOnly("com.mineinabyss:protocolburrito:0.3")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")

    compileOnly(project(":mobzy-core"))
    compileOnly(project(":mobzy-components"))
    compileOnly(project(":mobzy-nms-injection"))
}
