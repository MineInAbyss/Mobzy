package com.mineinabyss.mobzy.pathfinders

/*
fun hasLineOfSight(entity: Entity, dx: Double, dy: Double, dz: Double, distance: Double): Boolean {
    val loc: Location = entity.location
    val x: Double = (dx - loc.x) / distance
    val y: Double = (dy - loc.y) / distance
    val z: Double = (dz - loc.z) / distance
    var bb: BoundingBox = entity.boundingBox
    var i = 1
    while (i < distance) {
        bb = bb.shift(x, y, z)
        if (!entity.world.getBlockAt(mob, bb)) {
            return false
        }
        ++i
    }
    return true
}*/