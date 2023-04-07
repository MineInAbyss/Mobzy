plugins {
    id("com.mineinabyss.conventions.kotlin.jvm")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.nms.deobfuscated")
    id("com.mineinabyss.conventions.publication")
    kotlin("plugin.serialization")
}

dependencies {
    compileOnly(libs.kotlinx.serialization.json)

    compileOnly(project(":mobzy-core"))
    compileOnly(project(":mobzy-modelengine"))
}
