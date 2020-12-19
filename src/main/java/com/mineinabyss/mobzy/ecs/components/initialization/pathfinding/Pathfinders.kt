package com.mineinabyss.mobzy.ecs.components.initialization.pathfinding

import com.mineinabyss.geary.ecs.GearyComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//TODO clean up if https://github.com/Kotlin/kotlinx.serialization/issues/344 ever gets added
@Serializable
@SerialName("mobzy:pathfinders")
data class Pathfinders(
        val targets: Map<Double, PathfinderComponent>? = null,
        val goals: Map<Double, PathfinderComponent>? = null
): GearyComponent
