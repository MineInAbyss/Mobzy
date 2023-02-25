@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.mia.kotlin.jvm.get().pluginId)
    id(libs.plugins.mia.papermc.get().pluginId)
    id(libs.plugins.mia.publication.get().pluginId)
    id(libs.plugins.kotlinx.serialization.get().pluginId)
}

dependencies {
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.minecraft.plugin.modelengine)
    compileOnly(libs.minecraft.mccoroutine)

    compileOnly(project(":mobzy-core"))
}
