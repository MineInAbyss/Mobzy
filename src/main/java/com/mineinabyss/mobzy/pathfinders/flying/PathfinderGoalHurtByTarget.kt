package com.mineinabyss.mobzy.pathfinders.flying

import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal
import com.mineinabyss.mobzy.pathfinders.living
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityTargetEvent

class PathfinderGoalHurtByTarget(
        override val mob: CustomMob,
        val range: Double = mob.staticTemplate.followRange ?: 0.0
) : MobzyPathfinderGoal() {
    private lateinit var playerDamager: Player
    override fun shouldExecute(): Boolean {
        val damager = (nmsEntity.lastDamager ?: return false).living
        if (damager !is Player) return false
        playerDamager = damager
        return shouldKeepExecuting()
    }

    override fun shouldKeepExecuting(): Boolean = isPlayerValidTarget(playerDamager, range)

    override fun init() {
        nmsEntity.setGoalTarget(nmsEntity.lastDamager ?: return,
                EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY, true)
    }

    override fun reset() {
        target = null
    }
}