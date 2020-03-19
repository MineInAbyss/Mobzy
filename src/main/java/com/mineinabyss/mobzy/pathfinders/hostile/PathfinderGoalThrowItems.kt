package com.mineinabyss.mobzy.pathfinders.hostile

import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal
import kotlin.math.min

class PathfinderGoalThrowItems<T>(
        mob: T,
        val minChaseRad: Double,
        val minShootRad: Double,
        cooldown: Long = 3000L
) : MobzyPathfinderGoal(mob, cooldown = cooldown)
        where T : CustomMob,
              T : ItemThrowable {
    private var distance = 0.0

    override fun shouldExecute(): Boolean {
        return target != null && mob.distanceTo(target?: return false).also { distance = it } >
                //if there's no minChaseRad, stop pathfinder completely when we can't throw anymore
                if(minChaseRad <= 0) minShootRad else min(minChaseRad, minShootRad)
    }

    override fun shouldKeepExecuting() = shouldExecute() && !navigation.doneNavigating

    override fun init() {
        navigation.moveToEntity(target!!, 1.0)
    }

    override fun reset() {
        navigation.stopNavigation()
    }

    override fun execute() {
        val target = target ?: return

        if (distance < minChaseRad)
            navigation.stopNavigation()

        if (cooledDown && distance > minShootRad) {
            restartCooldown()
            (mob as ItemThrowable).throwItem(target)
        }
    }
}