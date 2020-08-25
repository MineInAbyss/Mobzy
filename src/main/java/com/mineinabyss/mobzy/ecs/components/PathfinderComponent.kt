package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.mobzy.api.nms.aliases.NMSPathfinderGoal
import org.bukkit.entity.Mob

interface PathfinderComponent : MobzyComponent {
    fun createPathfinder(mob: Mob): NMSPathfinderGoal
}