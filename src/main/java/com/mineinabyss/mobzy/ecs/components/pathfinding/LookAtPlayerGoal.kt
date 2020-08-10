package com.mineinabyss.mobzy.ecs.components.pathfinding

import com.mineinabyss.mobzy.ecs.components.MobzyComponent
import com.mineinabyss.mobzy.mobs.AnyCustomMob
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_16_R1.PathfinderGoal
import net.minecraft.server.v1_16_R1.PathfinderGoalLookAtPlayer

@Serializable
@Polymorphic
abstract class MobzyPathfinder<in T : AnyCustomMob> {
    abstract fun build(mob: T): PathfinderGoal
}

@Serializable
data class LookAtPlayerComponent(
        val radius: Float,
        val startChance: Float = 0.02f
) : MobzyComponent