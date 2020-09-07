package com.mineinabyss.mobzy.ecs.components.initialization.pathfinding

import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.mobzy.api.nms.aliases.NMSPathfinderGoal
import kotlinx.serialization.Serializable
import org.bukkit.entity.Mob

@Serializable
abstract class PathfinderComponent : MobzyComponent() {
    abstract fun build(mob: Mob): NMSPathfinderGoal
}