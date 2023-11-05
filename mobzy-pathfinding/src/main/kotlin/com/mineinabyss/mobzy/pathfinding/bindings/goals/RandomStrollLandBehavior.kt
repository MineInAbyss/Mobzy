package com.mineinabyss.mobzy.pathfinding.bindings.goals

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.random_stroll_land")
class RandomStrollLandBehavior(
    private val speedModifier: Double = 1.0,
    private val frequency: Float = 0.001f
) : PathfinderComponent() {
    override fun build(mob: Mob) = WaterAvoidingRandomStrollGoal(
        mob.toNMS<PathfinderMob>(),
        speedModifier,
        frequency
    )
}
