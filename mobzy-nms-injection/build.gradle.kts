val gearyVersion: String by project

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
}

dependencies {
    compileOnly("com.mineinabyss:geary-papermc-core:$gearyVersion")

    compileOnly(project(":mobzy-components"))
    compileOnly(project(":mobzy-pathfinding"))
}
