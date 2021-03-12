package com.mineinabyss.mobzy.ecs.goals.targetselectors.minecraft

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_16_R2.EntityHuman
import net.minecraft.server.v1_16_R2.PathfinderGoalNearestAttackableTarget
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:target.nearby_player")
class TargetNearbyPlayer : PathfinderComponent() {
    override fun build(mob: Mob) = PathfinderGoalNearestAttackableTarget(mob.toNMS(), EntityHuman::class.java, true)
}
