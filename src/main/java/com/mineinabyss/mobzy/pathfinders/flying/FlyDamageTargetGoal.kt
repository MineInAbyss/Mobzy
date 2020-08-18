package com.mineinabyss.mobzy.pathfinders.flying

import com.mineinabyss.mobzy.api.helpers.entity.canReach
import com.mineinabyss.mobzy.ecs.components.minecraft.attributes
import com.mineinabyss.mobzy.mobs.types.FlyingMob
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal

class FlyDamageTargetGoal(override val mob: FlyingMob) : MobzyPathfinderGoal() {
    override fun shouldExecute(): Boolean = mob.target != null && cooledDown

    override fun shouldKeepExecuting(): Boolean = false

    override fun execute() {
        restartCooldown()
        val target = mob.target ?: return
        val attackDamage: Double = mob.attributes?.attackDamage ?: return
        //if within range, harm
        if (entity.canReach(target)) target.damage(attackDamage, entity)
    }
}