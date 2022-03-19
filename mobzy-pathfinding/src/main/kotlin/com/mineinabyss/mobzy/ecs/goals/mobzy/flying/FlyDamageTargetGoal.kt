package com.mineinabyss.mobzy.ecs.goals.mobzy.flying

import com.mineinabyss.geary.papermc.GearyMCContext
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.MobAttributes
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.modelengine.playAnimation
import com.mineinabyss.mobzy.pathfinding.MobzyPathfinderGoal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Creature

context(GearyMCContext)
@Serializable
@SerialName("mobzy:behavior.flying_damage_target")
class FlyDamageTargetBehavior : PathfinderComponent() {
    override fun build(mob: Creature) = FlyDamageTargetGoal(mob)
}

context(GearyMCContext)
class FlyDamageTargetGoal(override val mob: Creature) : MobzyPathfinderGoal() {
    override fun shouldExecute(): Boolean = mob.target != null && cooledDown

    override fun shouldKeepExecuting(): Boolean = false

    override fun execute() {
        restartCooldown()
        val target = mob.target ?: return
        val attackDamage: Double = mob.toGeary().get<MobAttributes>()?.attackDamage ?: return
        //if within range, harm
        if (mob.location.distanceSquared(target.location) <= mob.toNMS().getMeleeAttackRangeSqr(target.toNMS())) {
            mob.playAnimation("attack", 0, 0, 1.0)
            target.damage(attackDamage, mob)

        }
    }
}
