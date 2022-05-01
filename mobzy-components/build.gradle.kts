plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.nms")
    kotlin("plugin.serialization")
}

dependencies {
    compileOnly(libs.kotlinx.serialization.json)

    compileOnly(libs.minecraft.plugin.modelengine)
}
