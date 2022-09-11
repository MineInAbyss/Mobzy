package com.mineinabyss.mobzy.ecs.goals.mobzy.flying

import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.mobzy.ecs.components.initialization.MobAttributes
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.modelengine.playAnimation
import com.mineinabyss.mobzy.pathfinding.MobzyPathfinderGoal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Mob

@Serializable
@SerialName("mobzy:behavior.flying_damage_target")
class FlyDamageTargetBehavior : PathfinderComponent() {
    override fun build(mob: Mob) = FlyDamageTargetGoal(mob)
}

class FlyDamageTargetGoal(override val mob: Mob) : MobzyPathfinderGoal() {
    override fun shouldExecute(): Boolean {
        val target = mob.target
        return target != null && cooledDown &&
                mob.location.distanceSquared(target.location) <= (1 + mob.width) * (1 + mob.width) + target.width
    }

    override fun shouldKeepExecuting(): Boolean = false

    override fun execute() {
        restartCooldown()
        val target = mob.target ?: return
        val attackDamage: Double = mob.toGeary().get<MobAttributes>()?.attackDamage ?: return
        //if within range, harm
        mob.playAnimation("attack", 0.0, 0.0, 1.0, false)
        target.damage(attackDamage, mob)
    }
}
