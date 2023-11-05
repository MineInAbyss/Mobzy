package com.mineinabyss.mobzy.pathfinding.bindings.goals

import com.mineinabyss.idofront.nms.aliases.NMSPlayer
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.avoid_player")
class AvoidPlayerBehavior(
    val radius: Float = 8f,
    val speed: Double = 1.0,
    val sprintSpeed: Double = 1.0
) : PathfinderComponent() {
    override fun build(mob: Mob) = AvoidEntityGoal(
        mob.toNMS<PathfinderMob>(),
        NMSPlayer::class.java, //TODO map of strings to NMS classes
        radius,
        speed,
        sprintSpeed
    )
}
