package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.mobzy.api.nms.aliases.NMSPathfinderGoal
import com.mineinabyss.geary.ecs.MobzyComponent
import kotlinx.serialization.Polymorphic
import org.bukkit.entity.Mob

@Polymorphic
interface PathfinderComponent : MobzyComponent {
    fun createPathfinder(mob: Mob): NMSPathfinderGoal
}