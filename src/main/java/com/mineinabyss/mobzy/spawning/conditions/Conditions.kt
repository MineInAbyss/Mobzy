package com.mineinabyss.mobzy.spawning.conditions

import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.components.get
import com.mineinabyss.geary.ecs.conditions.GearyCondition
import com.mineinabyss.mobzy.spawning.vertical.SpawnArea
import org.bukkit.Location

abstract class SpawnCondition : GearyCondition {
    abstract fun conditionsMet(on: SpawnArea): Boolean
}

abstract class LocationCondition : GearyCondition {
    abstract fun conditionsMet(on: Location): Boolean

    override fun conditionsMet(entity: GearyEntity): Boolean {
        return conditionsMet(entity.get<Location>() ?: return false)
    }
}
