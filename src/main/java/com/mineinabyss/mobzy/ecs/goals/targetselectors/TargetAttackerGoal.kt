package com.mineinabyss.mobzy.ecs.goals.targetselectors

import com.mineinabyss.geary.minecraft.access.geary
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.mobzy.ecs.components.initialization.MobAttributes
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal
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
    override fun build(mob: Mob) = TargetAttackerGoal(mob, range ?: geary(mob).get<MobAttributes>()?.followRange ?: 0.0)
}

class TargetAttackerGoal(
    override val mob: Mob,
    private val range: Double
) : MobzyPathfinderGoal(type = Type.TARGET) {
    private lateinit var playerDamager: Player

    override fun shouldExecute(): Boolean {
        val damager = (nmsEntity.lastDamager ?: return false).toBukkit()
        if (damager !is Player) return false
        playerDamager = damager
        return shouldKeepExecuting()
    }

    override fun shouldKeepExecuting(): Boolean = isPlayerValidTarget(playerDamager, range)

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
