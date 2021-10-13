package com.mineinabyss.mobzy.ecs.goals.mobzy.hostile

import com.mineinabyss.geary.ecs.api.GearyComponent
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.entities.createEntity
import com.mineinabyss.geary.minecraft.spawnGeary
import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.idofront.destructure.component2
import com.mineinabyss.idofront.destructure.component3
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.nms.entity.distanceSqrTo
import com.mineinabyss.idofront.nms.pathfindergoals.doneNavigating
import com.mineinabyss.idofront.nms.pathfindergoals.moveToEntity
import com.mineinabyss.idofront.nms.pathfindergoals.stopNavigation
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.pathfinding.MobzyPathfinderGoal
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.projectile.IProjectile
import net.minecraft.world.phys.Vec3D
import org.bukkit.entity.Creature
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Snowball
import org.bukkit.util.Vector
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

@Serializable
@SerialName("mobzy:behavior.throw_items")
class ThrowItemsBehavior(
    //TODO replace with serializable geary entity when that works
    val spawn: List<@Polymorphic GearyComponent>,
    val minChaseRad: Double = 0.0,
    val minThrowRad: Double = 7.0,
    val yOffset: Double = 0.0,
    val projectileSpeed: Float = 1.6f,
    val projectileAngularDiameter: Double = 12.0,
    val projectileCountPerThrow: Int = 1,
    val cooldown: Long = 3000L,
) : PathfinderComponent() {
    override fun build(mob: Mob) = ThrowItemsGoal(
        (mob as Creature),
        spawn.createEntity(),
        minChaseRad,
        minThrowRad,
        yOffset,
        projectileSpeed,
        projectileAngularDiameter,
        projectileCountPerThrow,
        cooldown,
    )
}

/**
 * Throws items at the target
 * @param minChaseRad Will not approach the target closer than this many blocks (unless other pathfinders define further behaviour).
 * @param minThrowRad The minimum radius at which to start throwing item at the target.
 * @param cooldown How long to wait between firing at the target.
 */
class ThrowItemsGoal(
    override val mob: Creature,
    private val prefab: GearyEntity,
    private val minChaseRad: Double,
    private val minThrowRad: Double,
    private val yOffset: Double,
    private val speed: Float,
    private val randomAngle: Double,
    private val count: Int,
    cooldown: Long = 3000L,
) : MobzyPathfinderGoal(cooldown = cooldown) {
    private var distance = 0.0

    override fun shouldExecute(): Boolean {
        return mob.target != null && mob.distanceSqrTo(mob.target ?: return false).also { distance = it } >
                //if there's no minChaseRad, stop pathfinder completely when we can't throw anymore
                (if (minChaseRad <= 0) minThrowRad else min(minChaseRad, minThrowRad)).pow(2)
    }

    override fun shouldKeepExecuting() = shouldExecute() && !navigation.doneNavigating

    override fun init() {
        mob.target?.let { navigation.moveToEntity(it, 1.0) }
    }

    override fun reset() {
        navigation.stopNavigation()
    }

    override fun execute() {
        val target = mob.target ?: return

        if (distance < minChaseRad)
            navigation.stopNavigation()

        if (cooledDown && distance > minThrowRad) {
            restartCooldown()
            throwItem(target)
        }
    }

    /** Throws the mob's defined item at the [target]*/
    private fun throwItem(target: LivingEntity) {
        repeat(count) {
            val entity = mob.eyeLocation.spawnGeary(prefab) ?: return@repeat
            val snowball = entity as? Snowball ?: return
            val loc = entity.location
            val (x, y, z) = loc

            val targetLoc = target.eyeLocation
            val dX = targetLoc.x - x
            val dY = targetLoc.y - y - 0.4
            val dZ = targetLoc.z - z
            snowball.toNMS().shootDirection(dX, dY, dZ, speed, randomAngle)
        }
    }
}

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
    setYawPitch(
        Math.toDegrees(atan2(directionVector.x, directionVector.z)).toFloat(),
        Math.toDegrees(atan2(directionVector.y, horizontalDistanceSqrt)).toFloat()
    )
    //TODO verify this is no longer needed
//    lastYaw = yRot
//    lastPitch = xRot
}

internal fun Double.toRadians(): Double {
    var result = this
    if (this < 0)
        result = this + 360.0
    return Math.toRadians(result)
}
