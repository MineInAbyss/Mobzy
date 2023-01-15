plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.nms")
    id("com.mineinabyss.conventions.publication")
    kotlin("plugin.serialization")
}

dependencies {
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    compileOnly(libs.kotlinx.coroutines)
    compileOnly(libs.minecraft.mccoroutine)

    compileOnly(libs.minecraft.plugin.modelengine)
    compileOnly(libs.minecraft.plugin.protocollib)
    compileOnly(myLibs.protocolburrito)

    compileOnly(project(":mobzy-core"))
    compileOnly(project(":mobzy-components"))
    compileOnly(project(":mobzy-nms-injection"))
}
