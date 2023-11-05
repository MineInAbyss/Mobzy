package com.mineinabyss.mobzy.pathfinding.bindings.selectors

import com.mineinabyss.idofront.nms.aliases.NMSPlayer
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:target.nearby_player")
class TargetNearbyPlayer : PathfinderComponent() {
    override fun build(mob: Mob) = NearestAttackableTargetGoal(mob.toNMS(), NMSPlayer::class.java, true)
}
