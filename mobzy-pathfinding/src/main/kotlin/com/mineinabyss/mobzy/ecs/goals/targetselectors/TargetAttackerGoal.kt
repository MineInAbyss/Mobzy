package com.mineinabyss.mobzy.ecs.goals.targetselectors

import com.mineinabyss.geary.papermc.GearyMCContext
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.mobzy.ecs.components.initialization.MobAttributes
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.pathfinding.MobzyPathfinderGoal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Creature
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityTargetEvent

@Serializable
@SerialName("mobzy:target.attacker")
class TargetAttacker(
    private val range: Double? = null
) : PathfinderComponent() {
    override fun build(mob: Creature) =
        TargetAttackerGoal(mob, range ?: mob.toGeary().get<MobAttributes>()?.followRange ?: 0.0)
}

class TargetAttackerGoal(
    override val mob: Creature,
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
    override fun shouldKeepExecuting(): Boolean = isPlayerValidTarget(playerDamager, range, ticksWaitAfterPlayerDeath = 1)

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
