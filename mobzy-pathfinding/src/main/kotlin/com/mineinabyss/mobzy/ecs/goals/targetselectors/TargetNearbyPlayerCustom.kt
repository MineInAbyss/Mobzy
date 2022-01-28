package com.mineinabyss.mobzy.ecs.goals.targetselectors

import com.mineinabyss.geary.minecraft.access.toGeary
import com.mineinabyss.idofront.messaging.logInfo
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.idofront.nms.entity.distanceSqrTo
import com.mineinabyss.mobzy.ecs.components.initialization.MobAttributes
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.pathfinding.MobzyPathfinderGoal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.EntityLiving
import org.bukkit.Statistic
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
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
class TargetNearbyPlayerCustom(
        private val range: Double? = null,
        private val ticksWaitAfterPlayerDeath: Int = 200
) : PathfinderComponent() {
    override fun build(mob: Mob) =
            TargetNearbyPlayerCustomGoal(mob, range ?: mob.toGeary().get<MobAttributes>()?.followRange ?: 0.0, ticksWaitAfterPlayerDeath)
}

class TargetNearbyPlayerCustomGoal(
        override val mob: Mob,
        private val range: Double,
        private val ticksWaitAfterPlayerDeath: Int = 200
) : MobzyPathfinderGoal(type = Type.d /* TARGET */) {
    private lateinit var playerDamager: Player

    override fun shouldExecute(): Boolean {
        assignTargetPlayer()

        val damager = (nmsEntity.lastDamager ?: return false).toBukkit()
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
        val possibleTargets = nmsEntity.toBukkit().getNearbyEntities(range, range, range)

        if (possibleTargets.isEmpty()) {
            reset()
            return
        }

        var chosenTargetIndex = -1

        for (i in possibleTargets.indices) {

            val targetEntity = possibleTargets[i]

            if (targetEntity is Player) {

                if (!isPlayerValidTarget(targetEntity, range, 200)) { continue }

                if (chosenTargetIndex == -1 ||  mob.distanceSqrTo(targetEntity) < mob.distanceSqrTo(possibleTargets[chosenTargetIndex])) {
                    chosenTargetIndex = i
                }
            }
        }

        if (chosenTargetIndex == -1) {
            reset()
            return
        }

        val chosenTarget = possibleTargets[chosenTargetIndex]

        nmsEntity.setGoalTarget(
                (chosenTarget as CraftPlayer).handle as EntityLiving,
                EntityTargetEvent.TargetReason.CLOSEST_PLAYER,
                true
        )
    }

}

