package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.idofront.nms.aliases.NMSPlayer
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal
import org.bukkit.entity.Creature

@Serializable
@SerialName("minecraft:behavior.avoid_player")
class AvoidPlayerBehavior(
    val radius: Float = 8f,
    val speed: Double = 1.0,
    val sprintSpeed: Double = 1.0
) : PathfinderComponent() {
    override fun build(mob: Creature) = AvoidEntityGoal(
        mob.toNMS(),
        NMSPlayer::class.java, //TODO map of strings to NMS classes
        radius,
        speed,
        sprintSpeed
    )
}
