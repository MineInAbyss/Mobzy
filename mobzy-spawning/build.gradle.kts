plugins {
    id("com.mineinabyss.conventions.kotlin.jvm")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.nms")
    id("com.mineinabyss.conventions.publication")
    kotlin("plugin.serialization")
}

dependencies {
    compileOnly(idofrontLibs.minecraft.mccoroutine)
    compileOnly(idofrontLibs.kotlinx.serialization.json)
    compileOnly(idofrontLibs.kotlinx.serialization.kaml)
    compileOnly(idofrontLibs.kotlinx.coroutines)
    compileOnly(idofrontLibs.kotlin.statistics) {
        exclude(group = "org.jetbrains.kotlin")
    }

    compileOnly(idofrontLibs.minecraft.plugin.worldguard) { exclude(group = "org.bukkit") }

    compileOnly(project(":mobzy-core"))
}
