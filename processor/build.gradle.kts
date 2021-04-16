import com.mineinabyss.mobzy.Deps

plugins {
    `java-library`
    kotlin("jvm")
    kotlin("kapt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:${Deps.serverVersion}") //Spigot
    compileOnly("org.spigotmc:spigot:${Deps.serverVersion}") // NMS

    implementation("com.squareup:kotlinpoet:1.7.2")
    implementation("com.squareup:kotlinpoet-metadata:1.7.2")
    implementation("com.squareup:kotlinpoet-metadata-specs:1.7.2")
    implementation("com.squareup:kotlinpoet-classinspector-elements:1.7.2")

    compileOnly("com.google.auto.service:auto-service:1.0-rc7")
    kapt("com.google.auto.service:auto-service:1.0-rc7")
}
