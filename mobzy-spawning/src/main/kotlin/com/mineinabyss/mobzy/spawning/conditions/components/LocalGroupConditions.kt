package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.EventScope
import com.mineinabyss.geary.systems.accessors.TargetScope
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
        (event.spawnInfo.localTypes[spawnType.prefab] ?: 0) < conf.max
}
