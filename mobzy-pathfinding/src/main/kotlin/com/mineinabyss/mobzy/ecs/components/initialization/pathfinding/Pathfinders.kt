package com.mineinabyss.mobzy.ecs.components.initialization.pathfinding

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A component that manages a list of [PathfinderComponent] which can be used to apply pathfinders upon entity creation.
 *
 * @param targets A list of [PathfinderComponent]s to be added as target selectors.
 * @param goals A list of [PathfinderComponent]s to be added as pathfinder goals.
 */
//TODO clean up if https://github.com/Kotlin/kotlinx.serialization/issues/344 ever gets added
@Serializable
@SerialName("mobzy:pathfinders")
data class Pathfinders(
    val targets: Map<Double, PathfinderComponent>? = null,
    val goals: Map<Double, PathfinderComponent>? = null
)
