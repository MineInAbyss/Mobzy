package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.mobzy.api.nms.aliases.NMSPathfinderGoal
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_16_R2.EntityCreature
import net.minecraft.server.v1_16_R2.PathfinderGoalPanic
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.panic_on_hit")
class PanicOnHitBehavior(
    private val speedModifier: Double = 1.0,
) : PathfinderComponent() {
    override fun build(mob: Mob): NMSPathfinderGoal = PathfinderGoalPanic(
        mob.toNMS<EntityCreature>(),
        speedModifier
    )
}
