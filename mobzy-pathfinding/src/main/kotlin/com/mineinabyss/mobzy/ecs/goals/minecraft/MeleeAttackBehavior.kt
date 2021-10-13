package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.idofront.nms.aliases.NMSEntityLiving
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.nms.entity.reachDistance
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.modelengine.playAnimation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack
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
        override fun a(target: NMSEntityLiving): Double = range ?: entity.reachDistance(target.bukkitEntity)

        override fun c() {
            super.c()
            entity.playAnimation("attack", 0, 0, 1.0)
        }
    }
}
