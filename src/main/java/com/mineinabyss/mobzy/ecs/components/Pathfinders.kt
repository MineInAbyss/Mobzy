package com.mineinabyss.mobzy.ecs.components

import kotlinx.serialization.*

//TODO clean up if https://github.com/Kotlin/kotlinx.serialization/issues/344 ever gets added
@Serializable
data class Pathfinders(
        val pathfinders: Map<Int, PathfinderComponent> = mapOf()
): MobzyComponent