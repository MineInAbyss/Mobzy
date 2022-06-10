import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.nms")
    id("com.mineinabyss.conventions.copyjar")
    id("com.mineinabyss.conventions.publication")
    id("com.mineinabyss.conventions.testing")
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
        mavenLocal()
        maven("https://repo.mineinabyss.com")
        maven("https://mvn.lumine.io/repository/maven-public/") // Model Engine
    }

    dependencies {
        val libs = rootProject.libs
        val mobzylibs = rootProject.mobzylibs

        compileOnly(mobzylibs.geary.papermc.core)

        implementation(libs.idofront.nms)
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {
    // MineInAbyss platform
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    compileOnly(libs.kotlinx.coroutines)
    compileOnly(libs.minecraft.mccoroutine)
    compileOnly(libs.koin.core)

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

tasks {
    shadowJar {
        archiveBaseName.set("Mobzy")
    }
}
