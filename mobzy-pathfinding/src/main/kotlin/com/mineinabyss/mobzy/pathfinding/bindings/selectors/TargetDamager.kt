package com.mineinabyss.mobzy.pathfinding.bindings.selectors

import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import org.bukkit.entity.Mob

@Serializable
@SerialName("minecraft:target.damager")
class TargetDamager : PathfinderComponent() { //TODO serializable ignore: Set<Class<*>>
    override fun build(mob: Mob) = HurtByTargetGoal(mob.toNMS<PathfinderMob>())
}
