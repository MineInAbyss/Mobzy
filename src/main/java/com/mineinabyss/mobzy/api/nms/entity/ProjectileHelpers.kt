package com.mineinabyss.mobzy.api.nms.entity

import net.minecraft.server.v1_16_R3.IProjectile
import net.minecraft.server.v1_16_R3.Vec3D
import org.bukkit.util.Vector
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Shoot the projectile towards a direction, with a certain speed and randomness
 * @param dX X direction
 * @param dY Y direction
 * @param dZ Z direction
 * @param speed The speed at which the projectile should travel
 * @param randomAngle The maximum random angle that can be applied to the projectile shooting direction. Set to 0 to aim directly at the target.
 */
//TODO: Implement the entity deltaPosition ticking ourselves at some point, to circumvent NMS.
fun IProjectile.shootDirection(dX: Double, dY: Double, dZ: Double, speed: Float, randomAngle: Double) {
    val directionVector = Vector(dX, dY, dZ).normalize()
    if (randomAngle != 0.0) {
        directionVector.rotateAroundX(Random.nextDouble(-randomAngle, randomAngle).toRadians())
        directionVector.rotateAroundY(Random.nextDouble(-randomAngle, randomAngle).toRadians())
        directionVector.rotateAroundZ(Random.nextDouble(-randomAngle, randomAngle).toRadians())
        directionVector.normalize()
    }
    directionVector.multiply(speed)

    mot = Vec3D(directionVector.x, directionVector.y, directionVector.z)

    // NMS stuff for orienting the projectile model towards the target
    val horizontalDistanceSqrt = sqrt(directionVector.x * directionVector.x + directionVector.z * directionVector.z)
    yaw = Math.toDegrees(atan2(directionVector.x, directionVector.z)).toFloat()
    pitch = Math.toDegrees(atan2(directionVector.y, horizontalDistanceSqrt)).toFloat()
    lastYaw = yaw
    lastPitch = pitch
}

internal fun Double.toRadians(): Double {
    var result = this
    if (this < 0)
        result = this + 360.0
    return Math.toRadians(result)
}