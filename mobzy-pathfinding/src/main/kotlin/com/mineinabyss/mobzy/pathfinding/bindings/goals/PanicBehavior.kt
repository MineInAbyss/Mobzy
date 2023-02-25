package com.mineinabyss.mobzy.pathfinding.bindings.goals

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.PanicGoal
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.panic")
class PanicBehavior(
    private val speedModifier: Double = 1.0,
) : PathfinderComponent() {
    override fun build(mob: Mob) = PanicGoal(
        mob.toNMS<PathfinderMob>(),
        speedModifier
    )
}
