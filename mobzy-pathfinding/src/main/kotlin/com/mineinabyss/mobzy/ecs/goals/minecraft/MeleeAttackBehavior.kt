package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.modelengine.playAnimation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.LivingEntity
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
        override fun checkAndPerformAttack(target: LivingEntity, squaredDistance: Double) {
            val width = mob.bbWidth
            val d = (1 + width) * (1 + width) + target.bbWidth
            if (squaredDistance <= d && ticksUntilNextAttack <= 0) {
                entity.playAnimation("attack", 0, 0, 1.0)
                resetAttackCooldown()
                mob.doHurtTarget(target)
            }
        }
    }
}
