package com.mineinabyss.mobzy

import com.mineinabyss.geary.addons.GearyPhase
import com.mineinabyss.geary.autoscan.autoscan
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.idofront.config.config
import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.idofront.platforms.Platforms
import com.mineinabyss.idofront.plugin.*
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.injection.MobzyTypesQuery
import com.mineinabyss.mobzy.modelengine.AnimationController
import com.mineinabyss.mobzy.spawning.MobCountManager
import com.mineinabyss.mobzy.spawning.MobzySpawning
import com.mineinabyss.mobzy.spawning.WorldGuardSpawnFlags
import com.mineinabyss.mobzy.systems.listeners.*
import com.mineinabyss.mobzy.systems.packets.MobzyPacketInterception
import com.mineinabyss.mobzy.systems.systems.ModelEngineSystem
import com.ticxo.modelengine.api.ModelEngineAPI
import org.bukkit.plugin.java.JavaPlugin

class MobzyPlugin : JavaPlugin() {
    override fun onLoad() {
        WorldGuardSpawnFlags.registerFlags()
        Platforms.load(this, "mineinabyss")
    }

    override fun onEnable() = actions {
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

        DI.add(object : MobzyModule {
            override val plugin: MobzyPlugin = this@MobzyPlugin
            override val config: MobzyConfig by config("config") {
                fromPluginPath(loadDefault = true)
            }
        })

        if (Plugins.isEnabled<ModelEngineAPI>()) {
            service<AnimationController>(ModelEngineSystem)
        }

        MobzyCommands()

        geary {
            if (mobzy.config.doMobSpawns) install(MobzySpawning)

            autoscan(classLoader, "com.mineinabyss") {
                all()
                custom<PathfinderComponent>()
            }
            on(GearyPhase.ENABLE) {
                MobzyPacketInterception.registerPacketInterceptors()

                logSuccess("Loaded types: ${MobzyTypesQuery.getKeys()}")
            }
        }
        geary.pipeline.addSystems(
            ModelEngineSystem
        )
    }
}
