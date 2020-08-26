package com.mineinabyss.mobzy.pathfinders.flying

import com.mineinabyss.mobzy.api.helpers.entity.distanceSqrTo
import com.mineinabyss.mobzy.api.helpers.entity.lookAt
import com.mineinabyss.mobzy.mobs.types.FlyingMob
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal
import net.minecraft.server.v1_16_R2.ControllerMove
import org.bukkit.Location
import kotlin.random.Random

open class IdleFlyGoal(override val mob: FlyingMob) : MobzyPathfinderGoal(cooldown = 100) {
    protected var targetLoc: Location? = null

    //if there isn't an operation to move somewhere, we can start looking for somewhere to fly
    override fun shouldExecute(): Boolean = mob.target == null

    override fun shouldKeepExecuting(): Boolean {
        val targetLoc = targetLoc ?: return false
        val dist = entity.distanceSqrTo(targetLoc)
        return mob.target == null && dist in 1.0..2000.0
    }

    override fun init() {
        val x = mob.locX + Random.nextInt(-16, 16)
        val y = mob.locY + Random.nextInt(-16, 12) //make it more likely to fly down
        val z = mob.locZ + Random.nextInt(-16, 16)
        val loc = Location(entity.world, x, y, z)
        if (!loc.block.isPassable) return
        targetLoc = loc
         //TODO make a wrapper for the controller and figure out the difference between it and navigation
    }

    override fun executeWhenCooledDown() {
        restartCooldown()
        val targetLoc = targetLoc ?: return
        entity.lookAt(targetLoc)
        moveController.moveTo(targetLoc.x, targetLoc.y, targetLoc.z, 1.0)
    }
}

//TODO move
fun ControllerMove.moveTo(x: Double, y: Double, z: Double, speed: Double) = a(x, y, z, speed)