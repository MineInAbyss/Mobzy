package com.mineinabyss.mobzy.pathfinders.hostile

import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack
import org.bukkit.entity.LivingEntity
import kotlin.math.min
import kotlin.random.Random

/**
 * Throws items at the target
 * @param minChaseRad Will not approach the target closer than this many blocks (unless other pathfinders define further behaviour).
 * @param minThrowRad The minimum radius at which to start throwing item at the target.
 * @param cooldown How long to wait between firing at the target.
 */
class PathfinderGoalThrowItems(
        override val mob: ItemThrowable,
        val minChaseRad: Double,
        val minThrowRad: Double,
        //TODO val accuracy: Double,
        cooldown: Long = 3000L
) : MobzyPathfinderGoal(cooldown = cooldown) {
    private var distance = 0.0

    override fun shouldExecute(): Boolean {
        return target != null && mob.distanceTo(target ?: return false).also { distance = it } >
                //if there's no minChaseRad, stop pathfinder completely when we can't throw anymore
                if (minChaseRad <= 0) minThrowRad else min(minChaseRad, minThrowRad)
    }

    override fun shouldKeepExecuting() = shouldExecute() && !navigation.doneNavigating

    override fun init() {
        navigation.moveToEntity(target!!, 1.0)
    }

    override fun reset() {
        navigation.stopNavigation()
    }

    override fun execute() {
        val target = target ?: return

        if (distance < minChaseRad)
            navigation.stopNavigation()

        if (cooledDown && distance > minThrowRad) {
            restartCooldown()
            throwItem(target)
        }
    }

    //TODO try not to rely on NMS at all here
    /** Throws the mob's defined item at the [target]*/
    fun throwItem(target: LivingEntity) {
        val world = entity.location.world ?: return
        val projectile = mob.createThrownEntity()
        val targetLoc = target.eyeLocation
        val dX = targetLoc.x - mob.locX
        val dY = targetLoc.y - mob.locY - 0.4
        val dZ = targetLoc.z - mob.locZ
        world.playSound(entity.location, Sound.ENTITY_SNOW_GOLEM_SHOOT, 1.0f, 1.0f / (Random.nextDouble(0.8, 1.2).toFloat()))
        projectile.shoot(dX, dY, dZ, 1.6f, 12.0f)
        if (mob.itemToThrow != null)
            projectile.setItem(CraftItemStack.asNMSCopy(mob.itemToThrow))
        (world as CraftWorld).handle.addEntity(projectile)
    }
}