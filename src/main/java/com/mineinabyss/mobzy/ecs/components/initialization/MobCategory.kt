package com.mineinabyss.mobzy.ecs.components.initialization

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.mobzy.spawning.MobCategories
import kotlinx.serialization.Serializable

@Serializable
data class MobCategory(
    val category: MobCategories
) : GearyComponent
