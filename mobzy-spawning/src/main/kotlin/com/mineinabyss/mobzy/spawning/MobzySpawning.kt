package com.mineinabyss.mobzy.spawning

import com.mineinabyss.geary.addons.GearyPhase
import com.mineinabyss.geary.addons.dsl.GearyAddonWithDefault
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.plugin.actions

val mobzySpawning by DI.observe<MobzySpawning>()

interface MobzySpawning {
    val spawnTask: SpawnTask
    val spawnRegistry: SpawnRegistry
    val config: SpawnConfig

    companion object: GearyAddonWithDefault<MobzySpawning> {
        override fun default(): MobzySpawning {
            TODO("Not yet implemented")
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
