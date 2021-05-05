package com.mineinabyss.mobzy.spawning.conditions

import com.mineinabyss.geary.ecs.api.conditions.GearyCondition
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.mobzy.spawning.MobCategory
import com.mineinabyss.mobzy.spawning.MobCountManager
import com.mineinabyss.mobzy.spawning.SpawnDefinition

object SpawnCapCondition : GearyCondition() {
    private val GearyEntity.spawnDefinition by get<SpawnDefinition>()

    override fun GearyEntity.check(): Boolean {
        return MobCountManager.isCategoryAllowed(spawnDefinition.prefab.get<MobCategory>() ?: return false)
    }
}