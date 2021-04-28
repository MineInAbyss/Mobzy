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

/**
 * # `mobzy:spawn.local_group`
 *
 * Checks that no more than [max] Bukkit entities of the same type as this one are within [radius] blocks during a
 * mob spawn.
 */
@Serializable
@SerialName("mobzy:spawn.local_group")
class LocalGroupConditions(
    private val max: Int,
    private val radius: Double
) : GearyCondition() {
    private val GearyEntity.location by get<Location>()
    private val GearyEntity.spawnDef by get<SpawnDefinition>()
    private val GearyEntity.spawnInfo by get<SpawnInfo>()

    override fun GearyEntity.check(): Boolean {
        //TODO considering we are now making this a suspend function, we could probably evaluate all mobs
        // simultaneously, then only wait for the sync ones to finish off.
        return spawnInfo.localMobs[spawnDef.entityType]?.get() ?: 0 < max
    }
}
