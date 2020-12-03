package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_16_R2.EntityHuman
import net.minecraft.server.v1_16_R2.PathfinderGoalAvoidTarget
import org.bukkit.entity.Creature
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.avoid_player")
class AvoidPlayerBehavior(
        val radius: Float = 8f,
        val speed: Double = 1.0,
        val sprintSpeed: Double = 1.0
) : PathfinderComponent() {
    override fun build(mob: Mob) = PathfinderGoalAvoidTarget(
            (mob as Creature).toNMS(),
            EntityHuman::class.java, //TODO map of strings to NMS classes
            radius,
            speed,
            sprintSpeed
    )
}