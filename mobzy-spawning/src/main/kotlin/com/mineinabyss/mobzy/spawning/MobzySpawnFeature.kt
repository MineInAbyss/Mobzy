package com.mineinabyss.mobzy.spawning

import com.mineinabyss.geary.modules.geary
import com.mineinabyss.idofront.config.config
import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.features.Configurable
import com.mineinabyss.idofront.features.FeatureDSL
import com.mineinabyss.idofront.features.FeatureWithContext
import com.mineinabyss.idofront.plugin.actions
import com.mineinabyss.mobzy.mobzy

val mobzySpawning by DI.observe<MobzySpawnFeature.Context>()

class MobzySpawnFeature : FeatureWithContext<MobzySpawnFeature.Context>(::Context) {
    class Context : Configurable<SpawnConfig> {
        override val configManager = config("spawning", mobzy.plugin.dataFolder.toPath(), SpawnConfig())
        val spawnTask = SpawnTask()
        val spawnRegistry = SpawnRegistry()
        val worldGuardFlags: WorldGuardSpawnFlags = DI.get<WorldGuardSpawnFlags>()
    }

    override val dependsOn = setOf("WorldGuard")

    override fun FeatureDSL.enable() = actions {
        DI.add<MobzySpawnFeature>(this@MobzySpawnFeature)
        "Spawns" {
            !"Load spawns" {
                context.spawnRegistry.reloadSpawns()
            }
            !"Start spawn task" {
                context.spawnTask.startTask()
            }
        }
    }

    override fun FeatureDSL.disable() {
        context.spawnTask.stopTask()
    }
}
