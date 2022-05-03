package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.modelengine.playAnimation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.PathfinderMob
import org.bukkit.entity.Mob
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal as NMSMeleeAttackGoal

@Serializable
@SerialName("minecraft:behavior.melee_attack")
class MeleeAttackBehavior(
    private val attackSpeed: Double = 1.0,
    private val seeThroughWalls: Boolean,
    private val range: Double? = null
) : PathfinderComponent() {
    override fun build(mob: Mob) = MeleeAttackGoal(mob)

    inner class MeleeAttackGoal(
        private val entity: Mob
    ) : NMSMeleeAttackGoal(entity.toNMS<PathfinderMob>(), attackSpeed, seeThroughWalls) {
        override fun start() {
            super.start()
            entity.playAnimation("attack", 0, 0, 1.0)
        }
    }
}
