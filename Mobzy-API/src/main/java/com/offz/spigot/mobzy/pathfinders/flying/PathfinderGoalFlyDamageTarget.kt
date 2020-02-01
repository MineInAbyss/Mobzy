package com.offz.spigot.mobzy.pathfinders.flying

import com.offz.spigot.mobzy.mobs.types.FlyingMob
import com.offz.spigot.mobzy.pathfinders.MobzyPathfinderGoal

class PathfinderGoalFlyDamageTarget(mob: FlyingMob) : MobzyPathfinderGoal(mob) {
    private var cooldown: Int = 0
    override fun shouldExecute(): Boolean {
        return target != null && --cooldown < 0
    }

    override fun shouldKeepExecuting(): Boolean = target != null

    override fun execute() {
        cooldown = 20
        val target = target ?: return
        val attackDamage: Double = mob.staticTemplate.attackDamage ?: return
        //if within range, harm
        if (mob.canReach(target)) target.damage(attackDamage, entity)
    }
}