package com.mineinabyss.mobzy.ecs.components.interaction

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Allows an entity to sex a mob with this component.
 */
@Serializable
@SerialName("mobzy:sexable")
@AutoscanComponent
class Sexable {
    private val isPregnant: Boolean = false
    private val acceptsPlayer: Boolean = true
}