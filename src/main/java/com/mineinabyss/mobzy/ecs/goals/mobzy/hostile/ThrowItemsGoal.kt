package com.mineinabyss.mobzy.ecs.goals.mobzy.hostile

import com.mineinabyss.geary.minecraft.actions.SpawnEntityAction
import com.mineinabyss.geary.minecraft.components.toBukkit
import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.idofront.destructure.component2
import com.mineinabyss.idofront.destructure.component3
import com.mineinabyss.mobzy.api.helpers.entity.distanceSqrTo
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.pathfindergoals.doneNavigating
import com.mineinabyss.mobzy.api.pathfindergoals.moveToEntity
import com.mineinabyss.mobzy.api.pathfindergoals.stopNavigation
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Sound
import org.bukkit.entity.*
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

@Serializable
@SerialName("mobzy:behavior.throw_items")
class ThrowItemsBehavior(
    val spawn: SpawnEntityAction = SpawnEntityAction(EntityType.SNOWBALL),
    val minChaseRad: Double = 0.0,
    val minThrowRad: Double = 7.0,
    val yOffset: Double = 0.0,
    val cooldown: Long = 3000L
) : PathfinderComponent() {
    init {
        if (!Snowball::class.java.isAssignableFrom(spawn.type.entityClass!!))
            error("Thrown entity must be a subclass of Snowball!")
    }

    override fun build(mob: Mob) = ThrowItemsGoal(
        (mob as Creature),
        spawn,
        minChaseRad,
        minThrowRad,
        yOffset,
        cooldown
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
    private val spawn: SpawnEntityAction,
    private val minChaseRad: Double,
    private val minThrowRad: Double,
    private val yOffset: Double = 0.0,
    //TODO val accuracy: Double,
    cooldown: Long = 3000L
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
        val world = mob.location.world ?: return
        val location = mob.eyeLocation
        val (x, y, z) = location

        val projectile = spawn.spawnAt(location.add(0.0, yOffset, 0.0))?.toBukkit<Snowball>()
            ?: error("Entity spawned from prefab was not a snowball")

        val targetLoc = target.eyeLocation
        val dX = targetLoc.x - x
        val dY = targetLoc.y - y - 0.4
        val dZ = targetLoc.z - z

        world.playSound(
            mob.location,
            Sound.ENTITY_SNOW_GOLEM_SHOOT,
            1.0f,
            1.0f / (Random.nextDouble(0.8, 1.2).toFloat())
        )

        projectile.toNMS().shoot(dX, dY, dZ, 1.6f, 12.0f)
    }
}
