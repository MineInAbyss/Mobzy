import com.mineinabyss.mineInAbyss
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.publication")
    kotlin("plugin.serialization")
    kotlin("kapt")
}

allprojects {
    apply(plugin = "java")

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf(
                "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
            )
        }
    }

    repositories {
        mineInAbyss()
    }

    dependencies {
        implementation("com.mineinabyss:idofront-nms:1.17.1-0.6.23") {
            exclude(group = "io.github.slimjar")
        }
    }
}

repositories {
    mavenCentral()
    maven("https://maven.sk89q.com/repo/") //WorldGuard/Edit
    maven("https://repo.dmulloy2.net/nexus/repository/public/") //ProtocolLib
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    // Other plugins
    compileOnly("com.mineinabyss:geary-platform-papermc:0.6.49")
    compileOnly("com.mineinabyss:geary-commons-papermc:0.1.2")
    compileOnly("com.mineinabyss:protocolburrito:0.2.20")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.2") { exclude(group = "org.bukkit") }
    compileOnly("com.comphenix.protocol:ProtocolLib:4.5.0")
    compileOnly("com.ticxo.modelengine:api:R2.1.6")
    compileOnly("com.mineinabyss:looty:0.3.19")

    // From Geary
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json")
    compileOnly("com.charleskorn.kaml:kaml")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
    compileOnly("com.github.okkero:skedule")

    // Shaded
    implementation("com.github.DRE2N:HeadLib:7e2d443678")
    slim("org.nield:kotlin-statistics")

    // Annotation processing
    compileOnly(project(":processor"))
    kapt(project(":processor"))
}

tasks {
    shadowJar {
        archiveBaseName.set("Mobzy")
    }
}
