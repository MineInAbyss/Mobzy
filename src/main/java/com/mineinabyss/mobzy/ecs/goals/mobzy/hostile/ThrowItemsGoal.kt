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
import com.mineinabyss.mobzy.api.nms.entity.shootDirection
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Creature
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Snowball
import kotlin.math.min
import kotlin.math.pow

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
