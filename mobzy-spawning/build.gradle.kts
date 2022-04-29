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
    //TODO move to idofront platform
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.0.1")
    runtimeOnly("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.0.1")
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    compileOnly(libs.kotlinx.coroutines)
    compileOnly(libs.kotlin.statistics) {
        exclude(group = "org.jetbrains.kotlin")
    }

    compileOnly("com.mineinabyss:geary-papermc-core:$gearyVersion")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.2") { exclude(group = "org.bukkit") }

    compileOnly(project(":mobzy-components"))
    compileOnly(project(":mobzy-core"))

}
