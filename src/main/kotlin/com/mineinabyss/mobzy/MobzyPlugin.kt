package com.mineinabyss.mobzy

import com.mineinabyss.geary.api.addon.GearyLoadPhase.ENABLE
import com.mineinabyss.geary.papermc.dsl.gearyAddon
import com.mineinabyss.idofront.platforms.IdofrontPlatforms
import com.mineinabyss.idofront.plugin.isPluginEnabled
import com.mineinabyss.idofront.plugin.registerEvents
import com.mineinabyss.idofront.plugin.registerService
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.modelengine.AnimationController
import com.mineinabyss.mobzy.spawning.MobCountManager
import com.mineinabyss.mobzy.spawning.WorldGuardSpawnFlags
import com.mineinabyss.mobzy.systems.listeners.GearySpawningListener
import com.mineinabyss.mobzy.systems.listeners.MobListener
import com.mineinabyss.mobzy.systems.listeners.MobzyECSListener
import com.mineinabyss.mobzy.systems.packets.MobzyPacketInterception
import com.mineinabyss.mobzy.systems.systems.ModelEngineSystem
import org.bukkit.plugin.java.JavaPlugin

class MobzyPlugin : JavaPlugin() {
    override fun onLoad() {
        WorldGuardSpawnFlags.registerFlags()
        IdofrontPlatforms.load(this, "mineinabyss")
    }

    override fun onEnable() {
        saveDefaultConfig()
        reloadConfig()

        //Register events
        registerEvents(
            MobListener,
            MobzyECSListener,
            MobCountManager,
            GearySpawningListener,
        )

        //Register commands
        MobzyCommands()

        val config = MobzyConfigImpl()
        registerService<MobzyConfig>(config)

        gearyAddon {
            autoScanAll()

            systems(ModelEngineSystem)

            if (isPluginEnabled("ModelEngine")) {
                registerService<AnimationController>(ModelEngineSystem)
            }

            // Autoscan the subclasses of PathfinderComponent
            autoScan<PathfinderComponent>()

            startup {
                ENABLE {
                    config.load()
                }
            }

            MobzyPacketInterception.registerPacketInterceptors()
        }
    }

    override fun onDisable() {
        server.scheduler.cancelTasks(this)
    }
}
