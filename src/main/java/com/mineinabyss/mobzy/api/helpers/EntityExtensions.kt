package com.mineinabyss.mobzy.api.helpers

import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import org.bukkit.Location
import org.bukkit.entity.Entity


/**
 * @param other Another entity.
 * @return The distance between the current entity and other entity's locations.
 */
fun Entity.distanceTo(other: Entity): Double = distanceTo(other.location)

/**
 * @param other Some location
 * @return The distance between the current entity and the other location
 */
fun Entity.distanceTo(other: Location): Double = location.distance(other)

/**
 * @param range the range to search within
 * @return a nearby player, or null if none are in the range
 */
fun Entity.findNearbyPlayer(range: Double) = world.toNMS().findNearbyPlayer(this.toNMS(), range)