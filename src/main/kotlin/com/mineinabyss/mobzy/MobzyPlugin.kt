package com.mineinabyss.mobzy

import com.mineinabyss.geary.addon.GearyLoadPhase.ENABLE
import com.mineinabyss.geary.addon.autoscan
import com.mineinabyss.geary.papermc.access.toGearyOrNull
import com.mineinabyss.geary.papermc.dsl.gearyAddon
import com.mineinabyss.idofront.config.config
import com.mineinabyss.idofront.config.singleConfig
import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.idofront.platforms.Platforms
import com.mineinabyss.idofront.plugin.*
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.injection.MobzyTypesQuery
import com.mineinabyss.mobzy.modelengine.AnimationController
import com.mineinabyss.mobzy.spawning.MobCountManager
import com.mineinabyss.mobzy.spawning.SpawnRegistry
import com.mineinabyss.mobzy.spawning.SpawnTask
import com.mineinabyss.mobzy.spawning.WorldGuardSpawnFlags
import com.mineinabyss.mobzy.systems.listeners.*
import com.mineinabyss.mobzy.systems.packets.MobzyPacketInterception
import com.mineinabyss.mobzy.systems.systems.ModelEngineSystem
import com.ticxo.modelengine.api.ModelEngineAPI
import org.bukkit.plugin.java.JavaPlugin
import org.koin.dsl.module

class MobzyPlugin : JavaPlugin() {
    override fun onLoad() {
        WorldGuardSpawnFlags.registerFlags()
        Platforms.load(this, "mineinabyss")
    }

    override fun onEnable() {
        //Register events
        listeners(
            MobListener,
            MobzyECSListener,
            MobCountManager,
            GearySpawningListener,
            RidableListener,
            TamableListener,
            //LeashingListener
        )

        startOrAppendKoin(module {
            singleConfig(config<MobzyConfig>("config") {
                fromPluginPath(loadDefault = true)
            })
        })

        gearyAddon {
            MobzyCommands()
            autoscan("com.mineinabyss") {
                all()
                custom<PathfinderComponent>()
            }

            systems(ModelEngineSystem)
            if (Plugins.isEnabled<ModelEngineAPI>()) {
                service<AnimationController>(ModelEngineSystem)
            }

            startup {
                ENABLE {
                    actions {
                        "Spawns" {
                            !"Load spawns" {
                                SpawnRegistry.reloadSpawns()
                            }
                            !"Start spawn task" {
                                SpawnTask.startTask()
                            }
                        }

                        logSuccess("Loaded types: ${MobzyTypesQuery.getKeys()}")
                        logSuccess("Successfully loaded config")
                    }

                }
            }

            MobzyPacketInterception.registerPacketInterceptors()
        }
    }

    override fun onDisable() {
        actions {
            "Stop spawn task" {
                SpawnTask.stopTask()
            }
            "Cancel all other tasks" {
                server.scheduler.cancelTasks(this@MobzyPlugin)
            }
            "Remove all geary entities" {
                server.worlds.forEach { world ->
                    world.entities.forEach { entity ->
                        // Encode geary entity data
                        entity.toGearyOrNull()?.removeEntity()
                    }
                }
            }
        }
    }
}
