package com.mineinabyss.mobzy.ecs.components.interaction

import com.mineinabyss.geary.ecs.GearyComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Prevents NPC entities from riding vehicles like boats.
 */
@Serializable
@SerialName("mobzy:prevent_riding")
class PreventRiding : GearyComponent
