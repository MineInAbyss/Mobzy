package com.mineinabyss.mobzy

import com.mineinabyss.geary.minecraft.dsl.GearyLoadPhase
import com.mineinabyss.geary.minecraft.dsl.gearyAddon
import com.mineinabyss.idofront.plugin.isPluginEnabled
import com.mineinabyss.idofront.plugin.registerEvents
import com.mineinabyss.idofront.plugin.registerService
import com.mineinabyss.idofront.slimjar.IdofrontSlimjar
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.injection.MobzyNMSTypeInjector
import com.mineinabyss.mobzy.modelengine.AnimationController
import com.mineinabyss.mobzy.spawning.MobCountManager
import com.mineinabyss.mobzy.spawning.WorldGuardSpawnFlags
import com.mineinabyss.mobzy.systems.listeners.AddPrefabFromNMSTypeSystem
import com.mineinabyss.mobzy.systems.listeners.GearySpawningListener
import com.mineinabyss.mobzy.systems.listeners.MobListener
import com.mineinabyss.mobzy.systems.listeners.MobzyECSListener
import com.mineinabyss.mobzy.systems.packets.MobzyPacketInterception
import com.mineinabyss.mobzy.systems.systems.AddPrefabsListener
import com.mineinabyss.mobzy.systems.systems.CopyNBTSystem
import com.mineinabyss.mobzy.systems.systems.ModelEngineSystem
import com.mineinabyss.mobzy.systems.systems.WalkingAnimationSystem
import org.bukkit.plugin.java.JavaPlugin

class MobzyPlugin : JavaPlugin() {
    override fun onLoad() {
        WorldGuardSpawnFlags.registerFlags()
        IdofrontSlimjar.loadToLibraryLoader(this)
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
            AddPrefabsListener(),
        )

        if (isPluginEnabled("ModelEngine")) {
            registerEvents(ModelEngineSystem)
            registerService<AnimationController>(ModelEngineSystem)
        }

        //Register commands
        MobzyCommands()

        val config = MobzyConfigImpl()
        registerService<MobzyConfig>(config)

        gearyAddon {
            systems(
                AddPrefabFromNMSTypeSystem(),
                WalkingAnimationSystem(),
                PathfinderAttachSystem(),
                MobCountManager.CountMobsSystem(),
                MobzyNMSTypeInjector,
                CopyNBTSystem()
            )

            autoscanAll()

            // Autoscan the subclasses of PathfinderComponent
            autoscan<PathfinderComponent>()

            startup {
                GearyLoadPhase.ENABLE {
                    config.load()
                }
            }
        }

        MobzyPacketInterception.registerPacketInterceptors()
    }

    override fun onDisable() {
        server.scheduler.cancelTasks(this)
    }
}
