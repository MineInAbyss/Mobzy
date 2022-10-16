plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.nms")
    id("com.mineinabyss.conventions.publication")
    kotlin("plugin.serialization")
}

repositories {
    maven("https://mvn.lumine.io/repository/maven-public/") { metadataSources { artifact() } } // Model Engine
}

dependencies {
    compileOnly(libs.kotlinx.serialization.json)

    compileOnly(libs.minecraft.plugin.modelengine)
}
