package com.mineinabyss.mobzy.features.nointeractions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `geary:no_vanilla_interactions`
 * On items, cancels all vanilla left click or right click interactions on an item.
 *
 * On mobs, cancels player damage, right clicks, and any form of movement.
 */
@Serializable
@SerialName("geary:no_vanilla_interactions")
class DisableMobInteractions

