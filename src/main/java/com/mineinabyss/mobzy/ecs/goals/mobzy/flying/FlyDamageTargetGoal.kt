package com.mineinabyss.mobzy.ecs.goals.mobzy.flying

import com.mineinabyss.mobzy.api.helpers.entity.canReach
import com.mineinabyss.mobzy.ecs.components.initialization.attributes
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Mob

@Serializable
@SerialName("mobzy:behavior.flying_damage_target")
class FlyDamageTargetBehavior: PathfinderComponent {
    override fun build(mob: Mob) = FlyDamageTargetGoal(mob)
}

class FlyDamageTargetGoal(override val mob: Mob) : MobzyPathfinderGoal() {
    override fun shouldExecute(): Boolean = mob.target != null && cooledDown

    override fun shouldKeepExecuting(): Boolean = false

    override fun execute() {
        restartCooldown()
        val target = mob.target ?: return
        val attackDamage: Double = mob.attributes?.attackDamage ?: return
        //if within range, harm
        if (mob.canReach(target)) target.damage(attackDamage, mob)
    }
}