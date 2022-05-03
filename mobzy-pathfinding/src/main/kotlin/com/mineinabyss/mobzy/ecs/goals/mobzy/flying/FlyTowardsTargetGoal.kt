package com.mineinabyss.mobzy.ecs.goals.mobzy.flying

import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.modelengine.isModelEngineEntity
import com.mineinabyss.mobzy.pathfinding.MobzyPathfinderGoal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Mob

@Serializable
@SerialName("mobzy:behavior.fly_towards_target")
class FlyTowardsTargetBehavior : PathfinderComponent() {
    override fun build(mob: Mob) = FlyTowardsTargetGoal(mob)
}

class FlyTowardsTargetGoal(override val mob: Mob) : MobzyPathfinderGoal(cooldown = 10, flags = listOf(Flag.MOVE)) {
    override fun shouldExecute(): Boolean = (mob.target != null)

    override fun shouldKeepExecuting(): Boolean = false

    override fun executeWhenCooledDown() {
        restartCooldown()
        val target = mob.target ?: return
        if (mob.isModelEngineEntity) {
            mob.lookAt(target)
        } else {
            mob.lookAt(target)
        }

        val l = target.eyeLocation.apply {
            //aim slightly higher when below target to fix getting stuck
            if (target.eyeLocation.y > mob.location.y) y += 1
        }
        nmsEntity.moveControl.setWantedPosition(l.x, l.y, l.z, 1.0)
    }
}
