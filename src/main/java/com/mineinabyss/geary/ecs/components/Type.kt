package com.mineinabyss.geary.ecs.components

import com.mineinabyss.geary.ecs.GearyComponent
import kotlinx.serialization.Serializable

@Serializable
class Type(
        val name: String
) : GearyComponent()