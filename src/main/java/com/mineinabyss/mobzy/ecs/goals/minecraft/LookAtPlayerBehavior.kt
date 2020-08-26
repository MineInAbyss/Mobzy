package com.mineinabyss.mobzy.ecs.goals.minecraft

import com.mineinabyss.mobzy.api.nms.aliases.NMSPlayer
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_16_R2.PathfinderGoalLookAtPlayer
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:behavior.look_at_player")
data class LookAtPlayerBehavior(
        val radius: Float,
        val startChance: Float = 0.02f
) : PathfinderComponent {
    override fun build(mob: Mob) = PathfinderGoalLookAtPlayer(mob.toNMS(), NMSPlayer::class.java, radius, startChance)
}