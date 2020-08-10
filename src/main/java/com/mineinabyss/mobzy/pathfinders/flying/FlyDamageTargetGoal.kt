package com.mineinabyss.mobzy.pathfinders.flying

import com.mineinabyss.mobzy.api.helpers.entity.canReach
import com.mineinabyss.mobzy.ecs.components.attributes
import com.mineinabyss.mobzy.mobs.types.FlyingMob
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal

class FlyDamageTargetGoal(override val mob: FlyingMob) : MobzyPathfinderGoal() {
    override fun shouldExecute(): Boolean = target != null && cooledDown

    override fun shouldKeepExecuting(): Boolean = false

    override fun execute() {
        restartCooldown()
        val target = target ?: return
        val attackDamage: Double = mob.type.attributes?.attackDamage ?: return
        //if within range, harm
        if (entity.canReach(target)) target.damage(attackDamage, entity)
    }
}