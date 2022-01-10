package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.ecs.accessors.EventScope
import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.get
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.api.systems.Handler
import com.mineinabyss.mobzy.spawning.SpawnType
import com.mineinabyss.mobzy.spawning.vertical.SpawnInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * # `mobzy:spawn.local_group`
 *
 * Checks that no more than [max] Bukkit entities of the same type as this one are within [radius] blocks during a
 * mob spawn.
 */
@Serializable
@SerialName("mobzy:check.spawn.local_group")
class LocalGroupConditions(
    val max: Int,
    val radius: Double
)

@AutoScan
class CapFull : GearyListener() {
    val TargetScope.conf by get<LocalGroupConditions>()
    val TargetScope.spawnType by get<SpawnType>()

    val EventScope.spawnInfo by get<SpawnInfo>()

    @Handler
    fun TargetScope.check(event: EventScope): Boolean =
        (event.spawnInfo.localMobs[spawnType.prefab.toEntity()?.get()]?.get() ?: 0) < conf.max
}
