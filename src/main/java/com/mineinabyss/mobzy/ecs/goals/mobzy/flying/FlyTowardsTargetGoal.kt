package com.mineinabyss.mobzy.ecs.goals.mobzy.flying

import com.mineinabyss.mobzy.api.helpers.entity.lookAt
import com.mineinabyss.mobzy.ecs.components.PathfinderComponent
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Mob

@Serializable
@SerialName("mobzy:behavior.fly_towards_target")
class FlyTowardsTargetBehavior : PathfinderComponent {
    override fun build(mob: Mob) = FlyTowardsTargetGoal(mob)
}

class FlyTowardsTargetGoal(override val mob: Mob) : MobzyPathfinderGoal() {
    override fun shouldExecute(): Boolean = (mob.target != null)

    override fun shouldKeepExecuting(): Boolean = mob.target != null

    override fun execute() {
        val target = mob.target ?: return
        mob.lookAt(target)

        val targetLoc = target.location
        moveController.a(targetLoc.x, targetLoc.y, targetLoc.z, 1.0) //TODO change to wrapper

        //aim slightly higher when below target to fix getting stuck
        if (targetLoc.y > mob.location.y)
            moveController.a(targetLoc.x, mob.location.y + 1, targetLoc.z, 1.0)
    }
}