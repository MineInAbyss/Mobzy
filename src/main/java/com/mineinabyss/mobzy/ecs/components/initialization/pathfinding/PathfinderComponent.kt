package com.mineinabyss.mobzy.ecs.components.initialization.pathfinding

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.mobzy.api.nms.aliases.NMSPathfinderGoal
import kotlinx.serialization.Serializable
import org.bukkit.entity.Mob

/**
 * The base class for serializable pathfinders. Subclasses define serializable properties to configure a pathfinder
 * and a [build] function to create a new [NMSPathfinderGoal] given that information.
 */
@Serializable
abstract class PathfinderComponent : GearyComponent {
    /** @return A new [NMSPathfinderGoal] for this [mob]. */
    abstract fun build(mob: Mob): NMSPathfinderGoal
}
