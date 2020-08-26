package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.geary.ecs.MobzyComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//TODO clean up if https://github.com/Kotlin/kotlinx.serialization/issues/344 ever gets added
@Serializable
@SerialName("mobzy:pathfinders")
data class Pathfinders(
        val pathfinders: Map<Double, PathfinderComponent> = mapOf()
): MobzyComponent