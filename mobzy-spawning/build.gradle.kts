val gearyVersion: String by project

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
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    compileOnly(libs.minecraft.skedule)
    compileOnly(libs.kotlinx.coroutines)
    compileOnly(libs.kotlin.statistics) {
        exclude(group = "org.jetbrains.kotlin")
    }

    compileOnly("com.mineinabyss:geary-papermc-core:$gearyVersion")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.2") { exclude(group = "org.bukkit") }

    compileOnly(project(":mobzy-components"))
    compileOnly(project(":mobzy-core"))

}
