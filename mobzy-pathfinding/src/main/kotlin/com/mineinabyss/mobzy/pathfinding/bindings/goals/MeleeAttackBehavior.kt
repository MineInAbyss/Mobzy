package com.mineinabyss.mobzy.pathfinding.bindings.goals

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.PathfinderMob
import org.bukkit.entity.Mob
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal as NMSMeleeAttackGoal

@Serializable
@SerialName("minecraft:behavior.melee_attack")
class MeleeAttackBehavior(
    private val attackSpeed: Double = 1.0,
    private val seeThroughWalls: Boolean = false,
    private val range: Double? = null
) : PathfinderComponent() {
    override fun build(mob: Mob) = MeleeAttackGoal(mob)

    inner class MeleeAttackGoal(
        private val entity: Mob
    ) : NMSMeleeAttackGoal(entity.toNMS<PathfinderMob>(), attackSpeed, seeThroughWalls) {

    }
}
