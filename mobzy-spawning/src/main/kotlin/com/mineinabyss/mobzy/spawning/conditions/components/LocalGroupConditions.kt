package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.events.CheckingListener
import com.mineinabyss.geary.systems.accessors.Pointers
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
class CapFull : CheckingListener() {
    val Pointers.conf by get<LocalGroupConditions>().on(source)
    val Pointers.spawnType by get<SpawnType>().on(source)

    val Pointers.spawnInfo by get<SpawnInfo>().on(event)

    override fun Pointers.check(): Boolean =
        spawnInfo.nearbyEntities(spawnType.prefab, conf.radius) < conf.max
}
