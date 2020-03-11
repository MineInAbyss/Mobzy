package com.mineinabyss.mobzy.pathfinders

import com.mineinabyss.mobzy.mobs.CustomMob

class PathfinderGoalMeleeAttackPitchLock(mob: CustomMob, private val speed: Double = 1.0) : MobzyPathfinderGoal(mob) {
    override fun shouldExecute(): Boolean = target != null && cooledDown

    override fun shouldKeepExecuting(): Boolean = false

    override fun execute() {
        lastHit = System.currentTimeMillis()
        val target = target ?: return
        mob.lookAtPitchLock(target)

        if (mob.canReach(target))
            target.damage(mob.staticTemplate.attackDamage ?: 0.0, entity)

        navigation.moveToEntity(target, speed)
    }
}