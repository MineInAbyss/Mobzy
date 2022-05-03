package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.idofront.nms.aliases.NMSPlayer
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.look_at_player")
data class LookAtPlayerBehavior(
    val radius: Float = 6.0f,
    val startChance: Float = 0.02f
) : PathfinderComponent() {
    override fun build(mob: Mob) = LookAtPlayerGoal(mob.toNMS(), NMSPlayer::class.java, radius, startChance)
}
