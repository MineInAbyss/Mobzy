package com.mineinabyss.mobzy.ecs.components.initialization.pathfinding

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.mobzy.api.nms.aliases.NMSPathfinderGoal
import kotlinx.serialization.Serializable
import org.bukkit.entity.Mob

@Serializable
abstract class PathfinderComponent : GearyComponent() {
    abstract fun build(mob: Mob): NMSPathfinderGoal
}