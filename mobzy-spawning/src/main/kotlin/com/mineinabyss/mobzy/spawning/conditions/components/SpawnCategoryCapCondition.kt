package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.ecs.accessors.EventScope
import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.building.get
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.annotations.Handler
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.mobzy.ecs.components.MobCategory
import com.mineinabyss.mobzy.spawning.MobCountManager
import com.mineinabyss.mobzy.spawning.SpawnType
import com.mineinabyss.mobzy.spawning.vertical.SpawnInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mobzy:ignore.spawn.category_cap")
class IgnoreSpawnCategoryCap()

@AutoScan
class SpawnCategoryCapCondition : GearyListener() {
    val TargetScope.spawnType by get<SpawnType>()

    val EventScope.spawnInfo by get<SpawnInfo>()

    init {
        target.not { has<IgnoreSpawnCategoryCap>() }
    }

    @Handler
    fun TargetScope.check(event: EventScope): Boolean {
        val category = spawnType.prefab.toEntity()?.get<MobCategory>() ?: return false
        return MobCountManager.isCategoryAllowed(category) &&
                (event.spawnInfo.localCategories[category] ?: 0) <
                (MobCountManager.categoryCounts[category]?.get() ?: 0)
    }
}
