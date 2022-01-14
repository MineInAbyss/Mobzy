import Com_mineinabyss_conventions_platform_gradle.Deps
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val serverVersion: String by project
val idofrontVersion: String by project
val gearyVersion: String by project


plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.copyjar")
    id("com.mineinabyss.conventions.publication")
    id("com.mineinabyss.conventions.testing")
    kotlin("plugin.serialization")
}

allprojects {
    apply(plugin = "java")

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf(
                "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
            )
            jvmTarget = "16"
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
    compileOnly(Deps.kotlin.stdlib)
    compileOnly(Deps.kotlinx.serialization.json)
    compileOnly(Deps.kotlinx.serialization.kaml)
    compileOnly(Deps.kotlinx.coroutines)
    compileOnly(Deps.minecraft.skedule)

    // Other plugins
    compileOnly("com.mineinabyss:geary-platform-papermc:$gearyVersion")

    // Shaded
    implementation(project(":mobzy-pathfinding"))
    implementation(project(":mobzy-systems"))
    implementation(project(":mobzy-components"))
    implementation(project(":mobzy-spawning"))
    implementation(project(":mobzy-nms-injection"))
    implementation(project(":mobzy-core"))

    // Testing
    testImplementation(Deps.`kotlin-statistics`)
    testImplementation("com.github.seeseemelk:MockBukkit-v1.17:1.13.0")
    testImplementation(Deps.kotlinx.serialization.json)
    testImplementation(Deps.kotlinx.serialization.kaml)
}

tasks {
    shadowJar {
        archiveBaseName.set("Mobzy")
    }
}
