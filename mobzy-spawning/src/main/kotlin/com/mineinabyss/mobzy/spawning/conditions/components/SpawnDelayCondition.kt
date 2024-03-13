package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.systems.builders.listener
import com.mineinabyss.geary.systems.query.ListenerQuery
import com.mineinabyss.idofront.serialization.DurationSerializer
import com.mineinabyss.mobzy.spawning.GlobalSpawnInfo
import com.mineinabyss.mobzy.spawning.mobzySpawning
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
@SerialName("mobzy:check.spawn.delay")
class SpawnDelay(
    @Serializable(with = DurationSerializer::class)
    val attemptEvery: Duration
)

@AutoScan
fun GearyModule.spawnDelayCondition() = listener(object : ListenerQuery() {
    val delay by source.get<SpawnDelay>()
}).check {
    val iterationMod = (delay.attemptEvery / mobzySpawning.config.spawnTaskDelay)
        .toInt().coerceAtLeast(1)
    GlobalSpawnInfo.iterationNumber % iterationMod == 0
}
