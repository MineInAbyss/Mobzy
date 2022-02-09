package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.building.get
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.annotations.Handler
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.idofront.serialization.DurationSerializer
import com.mineinabyss.mobzy.mobzyConfig
import com.mineinabyss.mobzy.spawning.GlobalSpawnInfo
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
class SpawnDelayCondition : GearyListener() {
    val TargetScope.delay by get<SpawnDelay>()

    @Handler
    fun TargetScope.check(): Boolean {
        val iterationMod = (delay.attemptEvery / mobzyConfig.spawnTaskDelay)
            .toInt().coerceAtLeast(1)
        return GlobalSpawnInfo.iterationNumber % iterationMod == 0
    }
}
