package com.mineinabyss.mobzy.ecs.goals.mobzy.flying

import com.mineinabyss.mobzy.api.helpers.entity.lookAt
import com.mineinabyss.mobzy.api.pathfindergoals.moveTo
import com.mineinabyss.mobzy.component1
import com.mineinabyss.mobzy.component2
import com.mineinabyss.mobzy.component3
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

class FlyTowardsTargetGoal(override val mob: Mob) : MobzyPathfinderGoal(cooldown = 10, type = Type.MOVE) {
    override fun shouldExecute(): Boolean = (mob.target != null)

    override fun shouldKeepExecuting(): Boolean = shouldExecute()

    override fun executeWhenCooledDown() {
        restartCooldown()
        val target = mob.target ?: return
        mob.lookAt(target)

        val (x, y, z) = target.eyeLocation

        //aim slightly higher when below target to fix getting stuck
        if (y > mob.location.y)
            moveController.moveTo(x, mob.location.y + 1, z, 1.0)
        else
            moveController.moveTo(x, y, z, 1.0)
    }
}