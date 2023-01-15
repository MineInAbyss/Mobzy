import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.nms")
    id("com.mineinabyss.conventions.copyjar")
    id("com.mineinabyss.conventions.publication")
    id("com.mineinabyss.conventions.testing")
    id("com.mineinabyss.conventions.autoversion")
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
        maven("https://repo.dmulloy2.net/nexus/repository/public/") //ProtocolLib
        maven("https://maven.enginehub.org/repo/") //WorldGuard/Edit
        maven("https://mvn.lumine.io/repository/maven-public/") { metadataSources { artifact() } } // Model Engine
        maven("https://jitpack.io")
    }

    dependencies {
        val libs = rootProject.libs
        val myLibs = rootProject.myLibs

        compileOnly(myLibs.geary.core)
        compileOnly(myLibs.geary.autoscan)
        compileOnly(myLibs.geary.serialization)
        compileOnly(myLibs.geary.prefabs)
        compileOnly(myLibs.geary.papermc.datastore)

        implementation(libs.bundles.idofront.core)
        implementation(libs.idofront.nms)
    }
}

dependencies {
    // MineInAbyss platform
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    compileOnly(libs.kotlinx.coroutines)
    compileOnly(libs.minecraft.mccoroutine)
    compileOnly(libs.idofront.di)
    compileOnly(libs.minecraft.plugin.modelengine)

    // Shaded
    api(project(":mobzy-pathfinding"))
    api(project(":mobzy-systems"))
    api(project(":mobzy-components"))
    api(project(":mobzy-spawning"))
    api(project(":mobzy-nms-injection"))
    api(project(":mobzy-core"))

    // Testing
    testImplementation(libs.kotlin.statistics)
    testImplementation(libs.minecraft.mockbukkit)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlinx.serialization.kaml)
}
