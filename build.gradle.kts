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
    maven("https://maven.sk89q.com/repo/") //WorldGuard/Edit
    maven("https://repo.dmulloy2.net/nexus/repository/public/") //ProtocolLib
    maven("https://jitpack.io")
    maven("https://mvn.lumine.io/repository/maven-public/") // Model Engine
}

dependencies {
    slim(kotlin("stdlib-jdk8"))
    // Other plugins
    compileOnly("com.mineinabyss:geary-platform-papermc:$gearyVersion")
    compileOnly("com.mineinabyss:geary-commons-papermc:0.1.2")
    compileOnly("com.mineinabyss:protocolburrito:0.2.25")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.2") { exclude(group = "org.bukkit") }
    compileOnly("com.comphenix.protocol:ProtocolLib:4.5.0")
    compileOnly("com.ticxo.modelengine:api:R2.2.0")
    compileOnly("com.mineinabyss:looty:0.3.19")

    // From Geary
    slim(Deps.kotlinx.serialization.json)
    slim(Deps.kotlinx.serialization.kaml)
    slim(Deps.kotlinx.coroutines)
    slim(Deps.minecraft.skedule)

    // Shaded
    implementation("com.github.DRE2N:HeadLib:7e2d443678")
    implementation(Deps.`kotlin-statistics`) {
        exclude(group = "org.jetbrains.kotlin")
    }

    // Testing
    testImplementation(Deps.`kotlin-statistics`)
    testImplementation("com.github.seeseemelk:MockBukkit-v1.17:1.10.1")// { isTransitive = false }
    testImplementation(Deps.kotlinx.serialization.json)
    testImplementation(Deps.kotlinx.serialization.kaml)
}

tasks {
    shadowJar {
        archiveBaseName.set("Mobzy")
//        relocate("com.mineinabyss.idofront", "com.mineinabyss.idofront.${project.group}.${project.name}")
//        relocate("io.github.slimjar", "io.github.slimjar.${project.group}.${project.name}")
    }
}
