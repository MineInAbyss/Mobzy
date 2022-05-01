plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.nms")
    kotlin("plugin.serialization")
}

repositories {
    maven("https://repo.dmulloy2.net/nexus/repository/public/") //ProtocolLib
    maven("https://jitpack.io")
    mavenLocal()
}

dependencies {
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    compileOnly(libs.kotlinx.coroutines)
    compileOnly(libs.minecraft.mccoroutine)

    compileOnly(libs.minecraft.plugin.modelengine)
    compileOnly(libs.minecraft.plugin.protocollib)
    compileOnly(mobzylibs.protocolburrito)

    compileOnly(project(":mobzy-core"))
    compileOnly(project(":mobzy-components"))
    compileOnly(project(":mobzy-nms-injection"))
}
