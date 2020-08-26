package com.mineinabyss.mobzy.ecs.goals.mobzy.flying

import com.mineinabyss.mobzy.api.helpers.entity.distanceSqrTo
import com.mineinabyss.mobzy.api.helpers.entity.lookAt
import com.mineinabyss.mobzy.component1
import com.mineinabyss.mobzy.component2
import com.mineinabyss.mobzy.component3
import com.mineinabyss.mobzy.ecs.components.PathfinderComponent
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_16_R2.ControllerMove
import org.bukkit.Location
import org.bukkit.entity.Mob
import kotlin.random.Random

@Serializable
@SerialName("mobzy:behavior.idle_fly")
class IdleFlyBehavior : PathfinderComponent {
    override fun build(mob: Mob) = IdleFlyGoal(mob)
}

open class IdleFlyGoal(override val mob: Mob) : MobzyPathfinderGoal(cooldown = 100, type = Type.MOVE) {
    protected var targetLoc: Location? = null

    //if there isn't an operation to move somewhere, we can start looking for somewhere to fly
    override fun shouldExecute(): Boolean = mob.target == null

    override fun shouldKeepExecuting(): Boolean {
        val targetLoc = targetLoc ?: return false
        val dist = mob.distanceSqrTo(targetLoc)
        return mob.target == null && dist in 1.0..2000.0
    }

    override fun init() {
        val (x, y, z) = mob.location
        val dx = x + Random.nextInt(-16, 16)
        val dy = y + Random.nextInt(-16, 12) //make it more likely to fly down
        val dz = z + Random.nextInt(-16, 16)
        val loc = Location(mob.world, dx, dy, dz)
        if (!loc.block.isPassable) return
        targetLoc = loc
        //TODO make a wrapper for the controller and figure out the difference between it and navigation
    }

    override fun executeWhenCooledDown() {
        restartCooldown()
        val targetLoc = targetLoc ?: return
        mob.lookAt(targetLoc)
        moveController.moveTo(targetLoc.x, targetLoc.y, targetLoc.z, 1.0)
    }
}

//TODO move
fun ControllerMove.moveTo(x: Double, y: Double, z: Double, speed: Double) = a(x, y, z, speed)