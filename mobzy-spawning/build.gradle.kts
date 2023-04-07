plugins {
    id("com.mineinabyss.conventions.kotlin.jvm")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.nms.deobfuscated")
    id("com.mineinabyss.conventions.publication")
    kotlin("plugin.serialization")
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

    compileOnly(project(":mobzy-core"))
}
