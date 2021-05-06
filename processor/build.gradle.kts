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

val serverVersion: String by project

dependencies {
    compileOnly("org.spigotmc:spigot-api:$serverVersion") //Spigot
    compileOnly("org.spigotmc:spigot:$serverVersion") // NMS

    implementation("com.squareup:kotlinpoet:1.8.0")
    implementation("com.squareup:kotlinpoet-metadata:1.8.0")
    implementation("com.squareup:kotlinpoet-metadata-specs:1.8.0")
    implementation("com.squareup:kotlinpoet-classinspector-elements:1.8.0")

    compileOnly("com.google.auto.service:auto-service:1.0")
    kapt("com.google.auto.service:auto-service:1.0")
}
