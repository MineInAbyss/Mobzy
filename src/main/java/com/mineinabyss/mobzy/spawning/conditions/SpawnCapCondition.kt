package com.mineinabyss.mobzy.spawning.conditions

import com.mineinabyss.geary.ecs.api.conditions.GearyCondition
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.mobzy.spawning.MobCategory
import com.mineinabyss.mobzy.spawning.MobCountManager

object SpawnCapCondition : GearyCondition() {
    private val GearyEntity.category by get<MobCategory>()

    override fun GearyEntity.check(): Boolean =
        MobCountManager.isCategoryAllowed(category)
}