package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:remove_when_far_away")
@AutoscanComponent
class RemoveWhenFarAway
