package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.nms.entity.reachDistance
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.ecs.systems.ModelEngineSystem.toModelEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_16_R3.EntityLiving
import net.minecraft.server.v1_16_R3.PathfinderGoalMeleeAttack
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

        override fun c() {
            super.c()
            entity.toModelEntity()?.allActiveModel?.values?.forEach { it.addState("attack", 0, 0, 1.0) }
        }
    }
}
