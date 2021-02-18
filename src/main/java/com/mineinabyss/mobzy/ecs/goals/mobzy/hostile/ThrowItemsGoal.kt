package com.mineinabyss.mobzy.ecs.goals.mobzy.hostile

import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.idofront.destructure.component2
import com.mineinabyss.idofront.destructure.component3
import com.mineinabyss.mobzy.api.helpers.entity.distanceSqrTo
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.nms.entity.shootDirection
import com.mineinabyss.mobzy.api.pathfindergoals.doneNavigating
import com.mineinabyss.mobzy.api.pathfindergoals.moveToEntity
import com.mineinabyss.mobzy.api.pathfindergoals.stopNavigation
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.mobs.types.ProjectileEntity
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal
import com.mineinabyss.mobzy.registration.MobzyTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Sound
import org.bukkit.entity.Creature
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

@Serializable
@SerialName("mobzy:behavior.throw_items")
class ThrowItemsBehavior(
    val type: String,
    val minChaseRad: Double = 0.0,
    val minThrowRad: Double = 7.0,
    val yOffset: Double = 0.0,
    val cooldown: Long = 3000L,
    val projectileSpeed: Float = 1.6f,
    val projectileRandomAngle: Double = 12.0,
) : PathfinderComponent() {
    //TODO evaluated lazily because MobTypes aren't registered while we are registering our mobs. Either somehow have a
    // 2-step process for registering MobTypes or make a lazy type serializer. 
    private val mobType: MobType by lazy {
        if (MobzyTypes[type].baseClass == "mobzy:projectile") MobzyTypes[type] else error("Template is not of type projectile")
    }

    override fun build(mob: Mob) = ThrowItemsGoal(
        (mob as Creature),
        mobType,
        minChaseRad,
        minThrowRad,
        yOffset,
        projectileSpeed,
        projectileRandomAngle,
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
    private val template: MobType,
    private val minChaseRad: Double,
    private val minThrowRad: Double,
    private val yOffset: Double,
    private val speed: Float,
    private val randomAngle: Double,
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

        val projectile = ProjectileEntity(template.nmsType, world.toNMS())
        projectile.setPosition(x, y + yOffset, z)

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

        projectile.shootDirection(dX, dY, dZ, speed, randomAngle)

        //TODO: Eventually have a standardized spawning system.
        // Cannot use the logic in location.spawnEntity though, that doesn't work for projectiles.
        // It needs to get added to the world like this.
        world.toNMS().addEntity(projectile)
    }
}
