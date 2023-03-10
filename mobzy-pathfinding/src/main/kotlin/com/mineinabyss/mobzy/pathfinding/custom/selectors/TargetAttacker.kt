package com.mineinabyss.mobzy.pathfinding.custom.selectors

import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.mobzy.pathfinding.MobzyPathfinderGoal
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityTargetEvent

@Serializable
@SerialName("mobzy:target.attacker")
class TargetAttacker(
    private val range: Double? = null
) : PathfinderComponent() {
    override fun build(mob: Mob) =
        TargetAttackerGoal(mob, range ?: mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE)?.value ?: 0.0)
}

class TargetAttackerGoal(
    override val mob: Mob,
    private val range: Double
) : MobzyPathfinderGoal(flags = listOf(Flag.TARGET)) {
    private lateinit var playerDamager: Player

    override fun shouldExecute(): Boolean {
        val damager = (nmsEntity.lastHurtByMob ?: return false).toBukkit()
        if (damager !is Player) return false
        playerDamager = damager
        return shouldKeepExecuting()
    }

    //ticksWaitAfterPlayerDeath = 1 because a player can be targeted when they attack the mob, no matter when they last died
    override fun shouldKeepExecuting(): Boolean =
        isPlayerValidTarget(playerDamager, range, ticksWaitAfterPlayerDeath = 1)

    override fun init() {
        nmsEntity.setTarget(
            nmsEntity.lastHurtByMob ?: return,
            EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY,
            true
        )
    }

    override fun reset() {
        mob.target = null
    }
}
