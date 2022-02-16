import Com_mineinabyss_conventions_platform_gradle.Deps

val gearyVersion: String by project

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    kotlin("plugin.serialization")
}

repositories {
    maven("https://maven.enginehub.org/repo/") //WorldGuard/Edit
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(Deps.kotlinx.serialization.json)
    compileOnly(Deps.kotlinx.serialization.kaml)
    compileOnly(Deps.minecraft.skedule)
    compileOnly(Deps.kotlinx.coroutines)
    compileOnly(Deps.`kotlin-statistics`) {
        exclude(group = "org.jetbrains.kotlin")
    }

    compileOnly("com.mineinabyss:geary-papermc-core:$gearyVersion")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.6") { exclude(group = "org.bukkit") }

    compileOnly(project(":mobzy-components"))
    compileOnly(project(":mobzy-core"))

}
