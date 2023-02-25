package com.mineinabyss.mobzy.pathfinding.custom.goals.flying

import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.idofront.destructure.component2
import com.mineinabyss.idofront.destructure.component3
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.modelengine.playAnimation
import com.mineinabyss.mobzy.pathfinding.MobzyPathfinderGoal
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.phys.Vec3
import org.bukkit.Location
import org.bukkit.entity.Mob
import org.bukkit.util.Vector
import kotlin.random.Random

@Serializable
@SerialName("mobzy:behavior.idle_fly")
class IdleFlyBehavior : PathfinderComponent() {
    override fun build(mob: Mob) = IdleFlyGoal(mob)
}

open class IdleFlyGoal(override val mob: Mob) : MobzyPathfinderGoal(cooldown = 100, flags = listOf(Flag.MOVE)) {
    //if there isn't an operation to move somewhere, we can start looking for somewhere to fly
    override fun shouldExecute(): Boolean {
        val move = nmsEntity.moveControl
        return !move.hasWanted() ||
                nmsEntity.distanceToSqr(move.wantedX, move.wantedY, move.wantedZ) !in 1.0..3600.0 ||
                mob.velocity == Vector()
    }

    override fun shouldKeepExecuting(): Boolean = false

    open fun findLoc(): Location? {
        val (x, y, z) = mob.location
        val dx = x + Random.nextInt(-16, 16)
        val dy = y + Random.nextInt(-16, 8) //make it more likely to fly down
        val dz = z + Random.nextInt(-16, 16)
        return Location(mob.world, dx, dy, dz).takeIf { it.block.isPassable }
    }
    override fun init() {
        val loc = findLoc() ?: return
        //TODO make a wrapper for the controller and figure out the difference between it and navigation

        mob.playAnimation("fly", 0.0, 0.0, 1.0, false)
        nmsEntity.moveControl.setWantedPosition(loc.x, loc.y, loc.z, 1.0)
        mob.lookAt(loc)
    }
}

val Mob.isStuck get() = toNMS().deltaMovement.lengthSqr < 0.0001

val Vec3.lengthSqr get() = x * x + y * y + z * z
