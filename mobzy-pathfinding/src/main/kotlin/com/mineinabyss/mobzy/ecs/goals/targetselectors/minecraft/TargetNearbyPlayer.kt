package com.mineinabyss.mobzy.ecs.goals.targetselectors.minecraft

import com.mineinabyss.idofront.nms.aliases.NMSPlayer
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import org.bukkit.entity.Creature

@Serializable
@SerialName("minecraft:target.nearby_player")
class TargetNearbyPlayer : PathfinderComponent() {
    override fun build(mob: Creature) = NearestAttackableTargetGoal(mob.toNMS(), NMSPlayer::class.java, true)
}
