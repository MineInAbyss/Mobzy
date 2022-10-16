package com.mineinabyss.mobzy.spawning.conditions.components

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.serialization.DurationSerializer
import com.mineinabyss.mobzy.MobzyConfig
import com.mineinabyss.mobzy.spawning.GlobalSpawnInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.component.inject
import kotlin.time.Duration

@Serializable
@SerialName("mobzy:check.spawn.delay")
class SpawnDelay(
    @Serializable(with = DurationSerializer::class)
    val attemptEvery: Duration
)

@AutoScan
class SpawnDelayCondition : GearyListener() {
    val config by inject<MobzyConfig>()

    private val TargetScope.delay by get<SpawnDelay>()

    @Handler
    fun TargetScope.check(): Boolean {
        val iterationMod = (delay.attemptEvery / config.spawnTaskDelay)
            .toInt().coerceAtLeast(1)
        return GlobalSpawnInfo.iterationNumber % iterationMod == 0
    }
}
