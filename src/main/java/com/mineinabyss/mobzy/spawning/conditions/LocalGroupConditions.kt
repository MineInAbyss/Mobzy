package com.mineinabyss.mobzy.spawning.conditions

import com.mineinabyss.geary.ecs.api.conditions.GearyCondition
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.spawning.SpawnDefinition
import com.mineinabyss.mobzy.spawning.vertical.SpawnInfo
import com.okkero.skedule.SynchronizationContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location

@Serializable
@SerialName("group")
class LocalGroupConditions(
    val max: Int,
    val radius: Double
) : GearyCondition() {
    val GearyEntity.location by get<Location>()
    val GearyEntity.spawnDef by get<SpawnDefinition>()
    val GearyEntity.spawnInfo by get<SpawnInfo>()

    override fun GearyEntity.check(): Boolean {
        //TODO considering we are now making this a suspend function, we could probably evaluate all mobs
        // simultaneously, then only wait for the sync ones to finish off.
        return spawnInfo.localMobs[spawnDef.entityType]?.get() ?: 0 < max
    }
}
