plugins {
    id(idofrontLibs.plugins.mia.kotlin.jvm.get().pluginId)
    id(idofrontLibs.plugins.mia.papermc.get().pluginId)
    id(idofrontLibs.plugins.mia.publication.get().pluginId)
    id(idofrontLibs.plugins.kotlinx.serialization.get().pluginId)
}

dependencies {
    compileOnly(idofrontLibs.kotlinx.serialization.json)
    compileOnly(idofrontLibs.minecraft.plugin.modelengine)
    compileOnly(idofrontLibs.minecraft.mccoroutine)

    compileOnly(project(":mobzy-core"))
}

/*configurations.all {
    resolutionStrategy.cacheChangingModulesFor( 0, "seconds")
}*/
