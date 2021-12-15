package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.ecs.accessors.EventResultScope
import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.events.handlers.CheckHandler
import com.mineinabyss.mobzy.ecs.components.MobCategory
import com.mineinabyss.mobzy.spawning.MobCountManager
import com.mineinabyss.mobzy.spawning.SpawnType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:ignore.spawn.category_cap")
class IgnoreSpawnCategoryCap()

@AutoScan
class SpawnCategoryCapCondition : GearyListener() {
    val ResultScope.spawnType by get<SpawnType>()

    init {
        not { has<IgnoreSpawnCategoryCap>() }
    }

    private inner class Check : CheckHandler() {
        override fun ResultScope.check(event: EventResultScope): Boolean {
            return MobCountManager.isCategoryAllowed(
                spawnType.prefab.toEntity()?.get<MobCategory>() ?: return false
            )
        }
    }
}
