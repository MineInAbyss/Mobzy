package com.mineinabyss.mobzy.pathfinding.components

import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.Goal
import org.bukkit.entity.Mob

/**
 * The base class for serializable pathfinders. Subclasses define serializable properties to configure a pathfinder
 * and a [build] function to create a new [Goal] given that information.
 */
@Serializable
abstract class PathfinderComponent {
    /** @return A new [Goal] for this [mob]. */
    abstract fun build(mob: Mob): Goal
}
