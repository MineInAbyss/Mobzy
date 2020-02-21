package com.offz.spigot.mobzy.pathfinders.flying

import com.offz.spigot.mobzy.mobs.types.FlyingMob
import com.offz.spigot.mobzy.pathfinders.MobzyPathfinderGoal

class PathfinderGoalFlyDamageTarget(mob: FlyingMob) : MobzyPathfinderGoal(mob) {

    override fun shouldExecute(): Boolean = target != null && cooledDown

    override fun shouldKeepExecuting(): Boolean = false

    override fun execute() {
        lastHit = System.currentTimeMillis()
        val target = target ?: return
        val attackDamage: Double = mob.staticTemplate.attackDamage ?: return
        //if within range, harm
        if (mob.canReach(target)) target.damage(attackDamage, entity)
    }
}