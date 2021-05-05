package com.mineinabyss.mobzy.spawning.conditions

import com.mineinabyss.geary.ecs.api.conditions.GearyCondition
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.mobzy.ecs.components.initialization.MobAttributes
import com.mineinabyss.mobzy.registration.toPrefab
import com.mineinabyss.mobzy.spawning.SpawnDefinition
import com.mineinabyss.mobzy.spawning.vertical.SpawnInfo
import javax.xml.stream.Location

//TODO
/*object EnoughSpaceCondition : GearyCondition() {
    private val GearyEntity.location by get<Location>()
    private val GearyEntity.spawnDef by get<SpawnDefinition>()
    private val GearyEntity.spawnInfo by get<SpawnInfo>()

    override fun GearyEntity.check(): Boolean {
        val attributes = spawnDef.entityType.toPrefab()?.get<MobAttributes>() ?: return false

        attributes.width
        return false
    }
}*/
