package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.nms.entity.reachDistance
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_16_R2.EntityLiving
import net.minecraft.server.v1_16_R2.PathfinderGoalMeleeAttack
import org.bukkit.entity.Creature
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.melee_attack")
class MeleeAttackBehavior(
    private val attackSpeed: Double = 1.0,
    private val seeThroughWalls: Boolean,
    private val range: Double? = null
) : PathfinderComponent() {
    override fun build(mob: Mob) = MeleeAttackGoal(mob as Creature)

    inner class MeleeAttackGoal(
        private val entity: Creature
    ) : PathfinderGoalMeleeAttack(entity.toNMS(), attackSpeed, seeThroughWalls) {
        override fun a(target: EntityLiving): Double = range ?: entity.reachDistance(target.bukkitEntity)
    }
}
