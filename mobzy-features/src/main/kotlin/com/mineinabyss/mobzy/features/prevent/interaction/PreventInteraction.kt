package com.mineinabyss.mobzy.features.prevent.interaction

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `mobzy:disable_mob_interactions`
 * Cancels player damage, right clicks, and any form of movement.
 */
@Serializable
@SerialName("mobzy:prevent.interaction")
class PreventInteraction(val type: Set<InteractionType> = setOf())

enum class InteractionType {
    ATTACK,
    RIGHT_CLICK,
}
