package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.events.CheckingListener
import com.mineinabyss.geary.systems.accessors.Pointers
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
class SpawnDelayCondition : CheckingListener() {
    private val Pointers.delay by get<SpawnDelay>().on(source)

    override fun Pointers.check(): Boolean {
        val iterationMod = (delay.attemptEvery / mobzySpawning.config.spawnTaskDelay)
            .toInt().coerceAtLeast(1)
        return GlobalSpawnInfo.iterationNumber % iterationMod == 0
    }
}
