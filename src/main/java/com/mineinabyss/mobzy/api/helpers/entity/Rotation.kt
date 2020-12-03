package com.mineinabyss.mobzy.api.helpers.entity

import org.bukkit.Location
import org.bukkit.entity.Entity


fun Entity.lookAt(x: Double, y: Double, z: Double) {
    val dirBetweenLocations = org.bukkit.util.Vector(x, y, z).subtract(location.toVector())
    val location = location
    location.direction = dirBetweenLocations
    setRotation(location.yaw, location.pitch)
}

/** Looks at [location] */
fun Entity.lookAt(location: Location) = lookAt(location.x, location.y, location.z)

/** Looks at [entity] */
fun Entity.lookAt(entity: Entity) = lookAt(entity.location)

fun Entity.lookAt(x: Double, z: Double) = lookAt(x, location.y, z)

fun Entity.lookAtPitchLock(location: Location) = lookAt(location.x, location.z)

fun Entity.lookAtPitchLock(entity: Entity) = lookAtPitchLock(entity.location)