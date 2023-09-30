package com.mineinabyss.mobzy.spawning

import com.mineinabyss.geary.addons.GearyPhase
import com.mineinabyss.geary.addons.dsl.GearyAddonWithDefault
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.idofront.config.config
import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.plugin.Plugins
import com.mineinabyss.idofront.plugin.actions
import com.mineinabyss.mobzy.mobzy
import com.sk89q.worldguard.bukkit.WorldGuardPlugin

val mobzySpawning by DI.observe<MobzySpawning>()

interface MobzySpawning {
    val spawnTask: SpawnTask
    val spawnRegistry: SpawnRegistry
    val config: SpawnConfig
    val worldGuardFlags: WorldGuardSpawnFlags

    companion object : GearyAddonWithDefault<MobzySpawning> {
        override fun default() = object : MobzySpawning {
            override val spawnTask = SpawnTask()
            override val spawnRegistry = SpawnRegistry()
            override val config: SpawnConfig by config("spawning") {
                mobzy.plugin.fromPluginPath(loadDefault = true)
            }
            override val worldGuardFlags: WorldGuardSpawnFlags = DI.get<WorldGuardSpawnFlags>()
        }

        override fun MobzySpawning.install() = actions {
            geary {
                on(GearyPhase.ENABLE) {
                    "Spawns" {
                        !"Load spawns" {
                            geary.pipeline.addSystem(spawnRegistry.SpawnTracker())
                            spawnRegistry.reloadSpawns()
                        }
                        !"Start spawn task" {
                            spawnTask.startTask()
                        }
                    }
                }
            }
        }
    }
}
