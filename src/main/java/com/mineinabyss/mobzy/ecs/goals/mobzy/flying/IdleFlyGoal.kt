package com.mineinabyss.mobzy.ecs.goals.mobzy.flying

import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.idofront.destructure.component2
import com.mineinabyss.idofront.destructure.component3
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.nms.entity.distanceSqrTo
import com.mineinabyss.idofront.nms.pathfindergoals.moveTo
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.ecs.systems.lengthSqr
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location
import org.bukkit.entity.Mob
import kotlin.random.Random

@Serializable
@SerialName("mobzy:behavior.idle_fly")
class IdleFlyBehavior : PathfinderComponent() {
    override fun build(mob: Mob) = IdleFlyGoal(mob)
}

open class IdleFlyGoal(override val mob: Mob) : MobzyPathfinderGoal(cooldown = 100, type = Type.a /* MOVE */) {
    protected var targetLoc: Location? = null

    //if there isn't an operation to move somewhere, we can start looking for somewhere to fly
    override fun shouldExecute(): Boolean = mob.target == null

    override fun shouldKeepExecuting(): Boolean {
        val targetLoc = targetLoc ?: return false
        val dist = mob.distanceSqrTo(targetLoc)
        return mob.target == null && dist in 1.0..2000.0 && !mob.isStuck
    }

    override fun init() {
        val (x, y, z) = mob.location
        val dx = x + Random.nextInt(-16, 16)
        val dy = y + Random.nextInt(-16, 8) //make it more likely to fly down
        val dz = z + Random.nextInt(-16, 16)
        val loc = Location(mob.world, dx, dy, dz)
        if (!loc.block.isPassable) return
        targetLoc = loc
        //TODO make a wrapper for the controller and figure out the difference between it and navigation
    }

    override fun executeWhenCooledDown() {
        restartCooldown()
        val (x, y, z) = targetLoc ?: return
//        mob.lookAt(x, y, z)
        moveController.moveTo(x, y, z, 1.0)
    }
}

val Mob.isStuck get() = toNMS().mot.lengthSqr < 0.0001
