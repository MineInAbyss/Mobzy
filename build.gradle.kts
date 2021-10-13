import Com_mineinabyss_conventions_platform_gradle.Deps
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val serverVersion: String by project
val idofrontVersion: String by project
val gearyVersion: String by project


plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.slimjar")
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
    // Download at runtime
    slim(kotlin("stdlib-jdk8"))
    slim(Deps.kotlinx.serialization.json)
    slim(Deps.kotlinx.serialization.kaml)
    slim(Deps.kotlinx.coroutines)
    slim(Deps.minecraft.skedule)

    // Other plugins
    compileOnly("com.mineinabyss:geary-platform-papermc:$gearyVersion")
    compileOnly("com.mineinabyss:looty:0.3.19")

    // Shaded
    implementation(project(":mobzy-pathfinding"))
    implementation(project(":mobzy-systems"))
    implementation(project(":mobzy-components"))
    implementation(project(":mobzy-spawning"))
    implementation(project(":mobzy-nms-injection"))
    implementation(project(":mobzy-core"))

    // Testing
    testImplementation(Deps.`kotlin-statistics`)
    testImplementation("com.github.seeseemelk:MockBukkit-v1.17:1.10.1")
    testImplementation(Deps.kotlinx.serialization.json)
    testImplementation(Deps.kotlinx.serialization.kaml)
}

tasks {
    shadowJar {
        archiveBaseName.set("Mobzy")
    }
}
