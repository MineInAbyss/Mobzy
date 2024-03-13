package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.systems.builders.listener
import com.mineinabyss.geary.systems.query.ListenerQuery
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
fun GearyModule.mobCapChecker() = listener(object : ListenerQuery() {
    val conf by source.get<LocalGroupConditions>()
    val spawnType by source.get<SpawnType>()
    val spawnInfo by event.get<SpawnInfo>()
}).check {
    spawnInfo.nearbyEntities(spawnType.prefab, conf.radius) < conf.max
}
