package com.mineinabyss.geary.ecs

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class MobzyComponent{
    @Transient
    var persist: Boolean = false
}