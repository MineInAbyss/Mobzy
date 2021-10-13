package com.mineinabyss.mobzy.ecs.goals.targetselectors.minecraft

import com.mineinabyss.idofront.nms.aliases.NMSEntityHuman
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:target.nearby_player")
class TargetNearbyPlayer : PathfinderComponent() {
    override fun build(mob: Mob) = PathfinderGoalNearestAttackableTarget(mob.toNMS(), NMSEntityHuman::class.java, true)
}
