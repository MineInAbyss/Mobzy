package com.mineinabyss.mobzy.ecs.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * > mobzy:remove_when_far_away
 *
 * Specifies this entity should get removed when it is far away from any player.
 */
@Serializable
@SerialName("mobzy:remove_when_far_away")
class RemoveWhenFarAway
