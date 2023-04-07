import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.mia.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.mia.papermc)
    alias(libs.plugins.mia.nms.deobfuscated)
    alias(libs.plugins.mia.nms.reobfuscate)
    alias(libs.plugins.mia.copyjar)
    alias(libs.plugins.mia.publication)
    alias(libs.plugins.mia.testing)
    alias(libs.plugins.mia.autoversion)
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

        compileOnly(myLibs.geary.papermc)

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
    api(project(":mobzy-features"))
    api(project(":mobzy-spawning"))
    api(project(":mobzy-core"))
    api(project(":mobzy-modelengine"))

    // Testing
    testImplementation(libs.kotlin.statistics)
    testImplementation(libs.minecraft.mockbukkit)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlinx.serialization.kaml)
}

configurations {
    findByName("runtimeClasspath")?.apply {
        exclude(group = "org.jetbrains.kotlin")
    }
}
