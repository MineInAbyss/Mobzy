plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.nms")
    kotlin("plugin.serialization")
}

repositories {
    maven("https://maven.enginehub.org/repo/") //WorldGuard/Edit
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(libs.minecraft.mccoroutine)
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    compileOnly(libs.kotlinx.coroutines)
    compileOnly(libs.kotlin.statistics) {
        exclude(group = "org.jetbrains.kotlin")
    }

    compileOnly(libs.minecraft.plugin.worldguard) { exclude(group = "org.bukkit") }

    compileOnly(project(":mobzy-components"))
    compileOnly(project(":mobzy-core"))

}
