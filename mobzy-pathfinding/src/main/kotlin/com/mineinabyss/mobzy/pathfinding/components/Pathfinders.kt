package com.mineinabyss.mobzy.pathfinding.components

import com.mineinabyss.geary.serialization.serializers.PolymorphicListAsMapSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A component that manages a list of [PathfinderComponent] which can be used to apply pathfinders upon entity creation.
 *
 * @param targets A list of [PathfinderComponent]s to be added as target selectors.
 * @param goals A list of [PathfinderComponent]s to be added as pathfinder goals.
 * @param override Whether to clear the entity's default pathfinders.
 */
@Serializable
@SerialName("mobzy:set.pathfinders")
data class Pathfinders(
    val targets: @Serializable(with = PolymorphicListAsMapSerializer::class) List<@Polymorphic PathfinderComponent>? = null,
    val goals: @Serializable(with = PolymorphicListAsMapSerializer::class) List<@Polymorphic PathfinderComponent>? = null,
    val noLookControl: Boolean = false,
    val override: Boolean = true,
)
