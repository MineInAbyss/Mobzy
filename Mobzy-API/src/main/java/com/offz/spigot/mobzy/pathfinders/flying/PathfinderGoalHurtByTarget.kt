package com.offz.spigot.mobzy.pathfinders.flying

import com.offz.spigot.mobzy.mobs.CustomMob
import com.offz.spigot.mobzy.pathfinders.MobzyPathfinderGoal
import com.offz.spigot.mobzy.pathfinders.living
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityTargetEvent

class PathfinderGoalHurtByTarget(mob: CustomMob, val range: Double = mob.staticTemplate.followRange ?: 0.0) : MobzyPathfinderGoal(mob) {
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