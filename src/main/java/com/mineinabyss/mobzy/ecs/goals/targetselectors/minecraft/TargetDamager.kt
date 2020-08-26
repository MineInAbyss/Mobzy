package com.mineinabyss.mobzy.ecs.goals.targetselectors.minecraft

import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_16_R2.PathfinderGoalHurtByTarget
import org.bukkit.entity.Creature
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:target.damager")
class TargetDamager : PathfinderComponent { //TODO serializable ignore: Set<Class<*>>
    override fun build(mob: Mob) = PathfinderGoalHurtByTarget((mob as Creature).toNMS())
}