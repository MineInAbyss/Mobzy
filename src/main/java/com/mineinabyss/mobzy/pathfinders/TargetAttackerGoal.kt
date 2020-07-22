package com.mineinabyss.mobzy.pathfinders

import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal
import com.mineinabyss.mobzy.api.pathfindergoals.living
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityTargetEvent

class TargetAttackerGoal(
        override val mob: CustomMob,
        private val range: Double = mob.template.attributes.followRange ?: 0.0
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