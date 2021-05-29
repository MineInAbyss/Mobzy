import com.mineinabyss.mineInAbyss
import com.mineinabyss.sharedSetup
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    idea
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "6.1.0"
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("kapt")
    id("org.jetbrains.dokka") version "1.4.30"
    id("com.mineinabyss.shared-gradle") version "0.0.6"
}

sharedSetup()

val kotlinVersion: String by project
val serverVersion: String by project

allprojects {
    apply(plugin = "kotlin")

    repositories {
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://repo.codemc.io/repository/nms/")
        mineInAbyss()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf(
                "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
            )
        }
    }

    dependencies {
        compileOnly("com.destroystokyo.paper:paper-api:$serverVersion")
        compileOnly("com.destroystokyo.paper:paper:$serverVersion") // NMS
        implementation("com.mineinabyss:idofront-nms:0.6.13")
    }
}

repositories {
    mavenCentral()
    jcenter()
    maven("https://erethon.de/repo/") //HeadLib
    maven("https://repo.dmulloy2.net/nexus/repository/public/") //ProtocolLib
    maven("https://maven.sk89q.com/repo/") //WorldGuard/Edit
    maven("https://jitpack.io")
    maven("https://mvn.lumine.io/repository/maven-releases/")
//    mavenLocal()
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))

    compileOnly(platform("com.mineinabyss:kotlinspice:${kotlinVersion}+"))
    compileOnly("com.github.okkero:skedule")
    compileOnly("org.nield:kotlin-statistics")
    compileOnly("com.mineinabyss:geary-spigot:0.3.29")
    compileOnly("com.mineinabyss:protocolburrito:0.1.12")

    compileOnly("de.erethon:headlib:3.0.2")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.2")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.5.0")
    compileOnly("com.ticxo.modelengine:api:R2.1.6")

    compileOnly(project(":processor"))
    kapt(project(":processor"))
}

tasks {
    shadowJar {
        archiveBaseName.set("Mobzy")

        minimize {
            exclude(dependency("de.erethon:headlib:3.0.2"))
            exclude(dependency("com.github.WesJD.AnvilGUI:anvilgui:5e3ab1f721"))
        }

        relocate("com.derongan.minecraft.guiy", "${project.group}.${project.name}.guiy".toLowerCase())
        relocate("com.mineinabyss.idofront", "${project.group}.${project.name}.idofront".toLowerCase())
    }

    build {
        dependsOn(shadowJar)
    }
}

publishing {
    mineInAbyss(project) {
        from(components["java"])
    }
}
