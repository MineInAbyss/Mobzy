package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.api.systems.GearyHandlerScope
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.mobzy.ecs.components.MobCategory
import com.mineinabyss.mobzy.spawning.MobCountManager
import com.mineinabyss.mobzy.spawning.SpawnType
import com.mineinabyss.mobzy.spawning.conditions.onCheckSpawn
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:check.spawn.category_cap")
object SpawnCategoryCapCondition : GearyListener() {
    val ResultScope.spawnType by get<SpawnType>()

    override fun GearyHandlerScope.register() {
        onCheckSpawn {
            MobCountManager.isCategoryAllowed(
                spawnType.prefab.toEntity()?.get<MobCategory>() ?: return@onCheckSpawn false
            )
        }
    }
}
