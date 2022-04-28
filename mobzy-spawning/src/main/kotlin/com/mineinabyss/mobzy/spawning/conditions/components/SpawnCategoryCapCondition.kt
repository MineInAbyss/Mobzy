package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.datatypes.family.family
import com.mineinabyss.geary.datatypes.family.MutableFamilyOperations.Companion.has
import com.mineinabyss.geary.datatypes.family.MutableFamilyOperations.Companion.not
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.EventScope
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.geary.systems.accessors.get
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
    private val TargetScope.spawnType by get<SpawnType>()
    private val TargetScope.notIgnored by family { not { has<IgnoreSpawnCategoryCap>() } }

    private val EventScope.spawnInfo by get<SpawnInfo>()

    @Handler
    fun TargetScope.check(event: EventScope): Boolean {
        val category = spawnType.prefab.toEntity()?.get<MobCategory>() ?: return false
        return MobCountManager.isCategoryAllowed(category) &&
                (event.spawnInfo.localCategories[category] ?: 0) <
                (MobCountManager.categoryCounts[category]?.get() ?: 0)
    }
}
