package com.mineinabyss.mobzy.pathfinding.custom.selectors

import com.mineinabyss.idofront.nms.aliases.NMSPathfinderMob
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import org.bukkit.entity.Mob

@Serializable
@SerialName("mobzy:target.attacker")
class TargetAttacker(
//    private val ignoredTypes: List<String> = listOf(),
) : PathfinderComponent() {
    override fun build(mob: Mob) = HurtByTargetGoal(mob.toNMS() as NMSPathfinderMob)
}
