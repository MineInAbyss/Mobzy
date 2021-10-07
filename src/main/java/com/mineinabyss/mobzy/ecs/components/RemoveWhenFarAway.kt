package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * > mobzy:remove_when_far_away
 *
 * Specifies this entity should get removed when it is far away from any player.
 */
@Serializable
@SerialName("mobzy:remove_when_far_away")
@AutoscanComponent
class RemoveWhenFarAway
