package com.mineinabyss.mobzy.api.helpers.entity

import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import org.bukkit.Location
import org.bukkit.entity.Entity
import kotlin.math.pow


/**
 * @param other Another entity.
 * @return The distance between the current entity and other entity's locations.
 */
fun Entity.distanceSqrTo(other: Entity): Double = distanceSqrTo(other.location)

/**
 * @param other Some location
 * @return The distance between the current entity and the other location
 */
fun Entity.distanceSqrTo(other: Location): Double = location.distanceSquared(other)

/**
 * @param range the range to search within
 * @return a nearby player, or null if none are in the range
 */
fun Entity.findNearbyPlayer(range: Double) = world.toNMS().findNearbyPlayer(this.toNMS(), range)

/** A custom definition of whether or not this entity should be able to reach and hit another one */
fun Entity.canReach(target: Entity) = distanceSqrTo(target) < reachDistance(target)

fun Entity.reachDistance(target: Entity) = width * width + target.width * target.width