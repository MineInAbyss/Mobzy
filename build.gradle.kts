import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val serverVersion: String by project
val idofrontVersion: String by project
val gearyVersion: String by project

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
                "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi",
            )
        }
    }

    repositories {
        maven("https://repo.mineinabyss.com")
    }

    dependencies {
        implementation("com.mineinabyss:idofront-nms:$idofrontVersion")
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    // MineInAbyss platform
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    compileOnly(libs.kotlinx.coroutines)
    compileOnly(libs.minecraft.skedule)
    compileOnly(libs.koin.core)

    // Other plugins
    compileOnly("com.mineinabyss:geary-papermc-core:$gearyVersion")

    // Shaded
    implementation(project(":mobzy-pathfinding"))
    implementation(project(":mobzy-systems"))
    implementation(project(":mobzy-components"))
    implementation(project(":mobzy-spawning"))
    implementation(project(":mobzy-nms-injection"))
    implementation(project(":mobzy-core"))

    // Testing
    testImplementation(libs.kotlin.statistics)
    testImplementation("com.github.seeseemelk:MockBukkit-v1.18:1.24.1")
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlinx.serialization.kaml)
}

tasks {
    shadowJar {
        archiveBaseName.set("Mobzy")
    }
}
