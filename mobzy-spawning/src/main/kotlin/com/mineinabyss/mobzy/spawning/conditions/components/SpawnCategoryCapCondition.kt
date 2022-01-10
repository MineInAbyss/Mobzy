package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.ecs.accessors.EventScope
import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.get
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.api.systems.Handler
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
    val TargetScope.spawnType by get<SpawnType>()

    init {
        target.not { has<IgnoreSpawnCategoryCap>() }
    }

    @Handler
    fun TargetScope.check(event: EventScope): Boolean {
        return MobCountManager.isCategoryAllowed(
            spawnType.prefab.toEntity()?.get<MobCategory>() ?: return false
        )
    }
}
