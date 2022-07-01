package com.mineinabyss.mobzy.ecs.components.initialization

import com.mineinabyss.idofront.serialization.DurationSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:on_spawn")
class OnSpawn(
    val animationName: String,
    val animationLength: @Serializable(with = DurationSerializer::class) kotlin.time.Duration,
)
