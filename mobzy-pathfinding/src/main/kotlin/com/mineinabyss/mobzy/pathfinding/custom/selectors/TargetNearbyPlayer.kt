package com.mineinabyss.mobzy.pathfinding.custom.selectors

import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.pathfinding.MobzyPathfinderGoal
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityTargetEvent

/**
 * This PathFinderComponent class allows mobs to Target Nearby Players that meet the necessary criteria.
 * Specifically, it builds the following Goal class, which encloses the actual logic of the targetting
 *
 * @param range the range of the mob to detect players to target
 * @param ticksWaitAfterPlayerDeath the number of ticks to wait after a player has died until they are a viable target
 */
@Serializable
@SerialName("mobzy:target.nearby_player")
class TargetNearbyPlayer(
    private val range: Double? = null,
    private val ticksWaitAfterPlayerDeath: Int = 200
) : PathfinderComponent() {
    override fun build(mob: Mob) =
        TargetNearbyPlayerCustomGoal(
            mob,
            range ?: mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE)?.value ?: 0.0,
            ticksWaitAfterPlayerDeath
        )
}

class TargetNearbyPlayerCustomGoal(
    override val mob: Mob,
    private val range: Double,
    private val ticksWaitAfterPlayerDeath: Int = 200
) : MobzyPathfinderGoal(flags = listOf(Flag.TARGET) /* TARGET */) {
    private lateinit var playerDamager: Player

    override fun shouldExecute(): Boolean {
        assignTargetPlayer()
        val damager = (nmsEntity.lastDamageSource?.entity ?: return false).toBukkit()
        if (damager !is Player) return false
        playerDamager = damager

        return shouldKeepExecuting()
    }

    override fun shouldKeepExecuting(): Boolean {
        return isPlayerValidTarget(playerDamager, range, ticksWaitAfterPlayerDeath)
    }


    override fun init() = assignTargetPlayer()

    override fun reset() {
        mob.target = null
    }

    /**
     * Finds and Assigns a player to be the target, if any are present.
     * Uses nmsEntity.setGoalTarget() to set target at end of function
     *
     * @return none, but uses return to exit early if no target is found
     */
    private fun assignTargetPlayer() {
        val targetPlayer = nmsEntity.toBukkit()
            .getNearbyEntities(range, range, range)
            .firstOrNull { it is Player } ?: return

        if (!isPlayerValidTarget(targetPlayer as Player, range, 200)) {
            reset()
            return
        }

        nmsEntity.setTarget(targetPlayer.toNMS(), EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true)
    }

}

