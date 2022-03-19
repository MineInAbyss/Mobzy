package com.mineinabyss.mobzy.ecs.goals.mobzy.flying

import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.modelengine.isModelEngineEntity
import com.mineinabyss.mobzy.pathfinding.MobzyPathfinderGoal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Creature

@Serializable
@SerialName("mobzy:behavior.fly_towards_target")
class FlyTowardsTargetBehavior : PathfinderComponent() {
    override fun build(mob: Creature) = FlyTowardsTargetGoal(mob)
}

class FlyTowardsTargetGoal(override val mob: Creature) : MobzyPathfinderGoal(cooldown = 10, flags = listOf(Flag.MOVE)) {
    override fun shouldExecute(): Boolean = (mob.target != null)

    override fun shouldKeepExecuting(): Boolean = shouldExecute()

    override fun executeWhenCooledDown() {
        restartCooldown()
        val target = mob.target ?: return
        if (mob.isModelEngineEntity) {
            mob.lookAt(target)
        } else {
            mob.lookAt(target)
        }

        //aim slightly higher when below target to fix getting stuck
        if (target.eyeLocation.y > mob.location.y)
            pathfinder.moveTo(target.eyeLocation.apply { y += 1 }, 1.0)
        else
            pathfinder.moveTo(target.eyeLocation, 1.0)
    }
}
