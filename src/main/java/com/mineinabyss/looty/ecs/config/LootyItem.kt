package com.mineinabyss.looty.ecs.config

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LootyItem (

)

data class MobType(
        @SerialName("name") private val _name: String? = null,
        @SerialName("staticComponents") private val _staticComponents: MutableSet<GearyComponent> = mutableSetOf(),
        @SerialName("components") private val _components: Set<GearyComponent> = setOf(),
        val targets: Map<Double, PathfinderComponent>? = null,
        val goals: Map<Double, PathfinderComponent>? = null)