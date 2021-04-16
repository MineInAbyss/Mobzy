import com.mineinabyss.miaSharedSetup
import com.mineinabyss.mobzy.Deps
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    idea
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "6.1.0"
    kotlin("jvm") version com.mineinabyss.mobzy.Deps.kotlinVersion
    kotlin("plugin.serialization") version com.mineinabyss.mobzy.Deps.kotlinVersion
    kotlin("kapt") version com.mineinabyss.mobzy.Deps.kotlinVersion
    id("org.jetbrains.dokka") version "1.4.30"
    id("com.mineinabyss.shared-gradle") version "0.0.3"
}

miaSharedSetup()

allprojects {
    repositories {
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://repo.codemc.io/repository/nms/")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf(
                "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
            )
        }
    }
}

repositories {
    mavenCentral()
    jcenter()
    maven("https://erethon.de/repo/") //HeadLib
    maven("https://repo.dmulloy2.net/nexus/repository/public/") //ProtocolLib
    maven("https://maven.sk89q.com/repo/") //WorldGuard/Edit
    maven("https://repo.mineinabyss.com/releases")
    maven("https://jitpack.io")
//    mavenLocal()
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:${Deps.serverVersion}")
    compileOnly("com.destroystokyo.paper:paper:${Deps.serverVersion}") // NMS
    compileOnly(kotlin("stdlib-jdk8"))

    compileOnly(platform("com.mineinabyss:kotlinspice:${Deps.kotlinVersion}+"))
    compileOnly("com.github.okkero:skedule")
    compileOnly("org.nield:kotlin-statistics")
    implementation("com.mineinabyss:idofront-nms:0.5.9")
    compileOnly("com.mineinabyss:geary-spigot:0.3.29")
    compileOnly("com.mineinabyss:protocolburrito:0.1.12")

    compileOnly("de.erethon:headlib:3.0.2")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.2")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.5.0")

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
