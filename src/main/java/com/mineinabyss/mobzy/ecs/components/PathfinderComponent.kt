package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.mobzy.api.nms.aliases.NMSPathfinderGoal
import kotlinx.serialization.Polymorphic
import org.bukkit.entity.Mob

@Polymorphic
interface PathfinderComponent : MobzyComponent {
    fun build(mob: Mob): NMSPathfinderGoal
}