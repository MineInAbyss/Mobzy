package com.mineinabyss.mobzy.pathfinders.flying

import com.mineinabyss.mobzy.mobs.types.FlyingMob
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal

class PathfinderGoalFlyDamageTarget(override val mob: FlyingMob) : MobzyPathfinderGoal() {
    override fun shouldExecute(): Boolean = target != null && cooledDown

    override fun shouldKeepExecuting(): Boolean = false

    override fun execute() {
        restartCooldown()
        val target = target ?: return
        val attackDamage: Double = mob.staticTemplate.attackDamage ?: return
        //if within range, harm
        if (mob.canReach(target)) target.damage(attackDamage, entity)
    }
}