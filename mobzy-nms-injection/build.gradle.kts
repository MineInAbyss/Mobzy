val gearyVersion: String by project

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.nms")
}

dependencies {
    compileOnly("com.mineinabyss:geary-papermc-core:$gearyVersion")

    compileOnly(project(":mobzy-core"))
    compileOnly(project(":mobzy-components"))
    compileOnly(project(":mobzy-pathfinding"))
}
