package com.mineinabyss.mobzy.pathfinders

import com.mineinabyss.mobzy.api.nms.aliases.living
import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityTargetEvent

class TargetNearbyPlayerGoal(override val mob: CustomMob) : MobzyPathfinderGoal() {
    private lateinit var playerDamager: Player
    override fun shouldExecute(): Boolean {
        val damager = (nmsEntity.lastDamager ?: return false).living
        if (damager !is Player) return false
        playerDamager = damager
        return isPlayerValidTarget(playerDamager)
    }

    override fun shouldKeepExecuting(): Boolean = isPlayerValidTarget(playerDamager)

    override fun init() {
        nmsEntity.setGoalTarget(nmsEntity.lastDamager ?: return,
                EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY, true)
    }

    override fun reset() {
        target = null
    }
}