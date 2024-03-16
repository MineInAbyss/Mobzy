import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(idofrontLibs.plugins.mia.kotlin.jvm)
    alias(idofrontLibs.plugins.kotlinx.serialization)
    alias(idofrontLibs.plugins.mia.papermc)
    alias(idofrontLibs.plugins.mia.nms)
    alias(idofrontLibs.plugins.mia.copyjar)
    alias(idofrontLibs.plugins.mia.publication)
    alias(idofrontLibs.plugins.mia.testing)
    alias(idofrontLibs.plugins.mia.autoversion)
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.dokka")

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf(
                "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
            )
        }
    }

    repositories {
        mavenCentral()
        maven("https://repo.mineinabyss.com/releases")
        maven("https://repo.mineinabyss.com/snapshots")
        maven("https://repo.dmulloy2.net/nexus/repository/public/") //ProtocolLib
        maven("https://maven.enginehub.org/repo/") //WorldGuard/Edit
        maven("https://mvn.lumine.io/repository/maven-public/")
        maven("https://jitpack.io")
        mavenLocal()
    }

    dependencies {
        val idofrontLibs = rootProject.idofrontLibs
        val libs = rootProject.libs

        compileOnly(libs.geary.papermc)

        implementation(idofrontLibs.bundles.idofront.core)
        implementation(idofrontLibs.idofront.nms)
    }
}

dependencies {
    // MineInAbyss platform
    compileOnly(idofrontLibs.kotlin.stdlib)
    compileOnly(idofrontLibs.kotlinx.serialization.json)
    compileOnly(idofrontLibs.kotlinx.serialization.kaml)
    compileOnly(idofrontLibs.kotlinx.coroutines)
    compileOnly(idofrontLibs.minecraft.mccoroutine)
    compileOnly(idofrontLibs.idofront.di)

    // Shaded
    api(project(":mobzy-pathfinding"))
    api(project(":mobzy-features"))
    api(project(":mobzy-spawning"))
    api(project(":mobzy-core"))
    api(project(":mobzy-modelengine"))

    // Testing
    testImplementation(idofrontLibs.kotlin.statistics)
    testImplementation(idofrontLibs.minecraft.mockbukkit)
    testImplementation(idofrontLibs.kotlinx.serialization.json)
    testImplementation(idofrontLibs.kotlinx.serialization.kaml)
}
