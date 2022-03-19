package com.mineinabyss.mobzy.ecs.goals.mobzy.flying

import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.idofront.destructure.component2
import com.mineinabyss.idofront.destructure.component3
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.modelengine.playAnimation
import com.mineinabyss.mobzy.pathfinding.MobzyPathfinderGoal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.phys.Vec3
import org.bukkit.Location
import org.bukkit.entity.Creature
import org.bukkit.entity.Mob
import kotlin.random.Random

@Serializable
@SerialName("mobzy:behavior.idle_fly")
class IdleFlyBehavior : PathfinderComponent() {
    override fun build(mob: Creature) = IdleFlyGoal(mob)
}

open class IdleFlyGoal(override val mob: Creature) : MobzyPathfinderGoal(cooldown = 100, flags = listOf(Flag.MOVE)) {
    protected var targetLoc: Location? = null

    //if there isn't an operation to move somewhere, we can start looking for somewhere to fly
    override fun shouldExecute(): Boolean = mob.target == null

    override fun shouldKeepExecuting(): Boolean {
        val targetLoc = targetLoc ?: return false
        val dist = mob.location.distanceSquared(targetLoc)
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

        mob.playAnimation("fly", 0, 0, 1.0)
    }

    override fun executeWhenCooledDown() {
        restartCooldown()
//        mob.lookAt(x, y, z)
        pathfinder.moveTo(targetLoc ?: return, 1.0)
    }
}

val Mob.isStuck get() = toNMS().deltaMovement.lengthSqr < 0.0001

val Vec3.lengthSqr get() = x * x + y * y + z * z
