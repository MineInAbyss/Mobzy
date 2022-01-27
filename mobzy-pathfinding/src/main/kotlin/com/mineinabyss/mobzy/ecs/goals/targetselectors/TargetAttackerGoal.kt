package com.mineinabyss.mobzy.ecs.goals.targetselectors

import com.mineinabyss.geary.minecraft.access.toGeary
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.mobzy.ecs.components.initialization.MobAttributes
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.pathfinding.MobzyPathfinderGoal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityTargetEvent

@Serializable
@SerialName("mobzy:target.attacker")
class TargetAttacker(
    private val range: Double? = null
) : PathfinderComponent() {
    override fun build(mob: Mob) =
        TargetAttackerGoal(mob, range ?: mob.toGeary().get<MobAttributes>()?.followRange ?: 0.0)
}

class TargetAttackerGoal(
    override val mob: Mob,
    private val range: Double
) : MobzyPathfinderGoal(type = Type.d /* TARGET */) {
    private lateinit var playerDamager: Player

    override fun shouldExecute(): Boolean {
        val damager = (nmsEntity.lastDamager ?: return false).toBukkit()
        if (damager !is Player) return false
        playerDamager = damager
        return shouldKeepExecuting()
    }

    //ticksWaitAfterPlayerDeath = 1 because a player can be targeted when they attack the mob, no matter when they last died
    override fun shouldKeepExecuting(): Boolean = isPlayerValidTarget(playerDamager, range, ticksWaitAfterPlayerDeath = 1)

    override fun init() {
        nmsEntity.setGoalTarget(
            nmsEntity.lastDamager ?: return,
            EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY,
            true
        )
    }

    override fun reset() {
        mob.target = null
    }
}
