package com.mineinabyss.mobzy

import com.mineinabyss.geary.minecraft.dsl.GearyLoadPhase
import com.mineinabyss.geary.minecraft.dsl.gearyAddon
import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.plugin.isPluginEnabled
import com.mineinabyss.idofront.plugin.registerEvents
import com.mineinabyss.idofront.plugin.registerService
import com.mineinabyss.idofront.serialization.SerializablePrefabItemService
import com.mineinabyss.idofront.slimjar.IdofrontSlimjar
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.injection.MobzyNMSTypeInjector
import com.mineinabyss.mobzy.spawning.MobCountManager
import com.mineinabyss.mobzy.spawning.WorldGuardSpawnFlags
import com.mineinabyss.mobzy.systems.listeners.GearySpawningListener
import com.mineinabyss.mobzy.systems.listeners.MobListener
import com.mineinabyss.mobzy.systems.listeners.MobzyECSListener
import com.mineinabyss.mobzy.systems.packets.MobzyPacketInterception
import com.mineinabyss.mobzy.systems.systems.AddPrefabsListener
import com.mineinabyss.mobzy.systems.systems.CopyNBTSystem
import com.mineinabyss.mobzy.systems.systems.ModelEngineSystem
import com.mineinabyss.mobzy.systems.systems.WalkingAnimationSystem
import org.bukkit.plugin.java.JavaPlugin

@ExperimentalCommandDSL
class MobzyPlugin : JavaPlugin() {
    override fun onLoad() {
        logger.info("On load has been called")
        WorldGuardSpawnFlags.registerFlags()
    }

    override fun onEnable() {
        IdofrontSlimjar.loadToLibraryLoader(this)

        //Plugin startup logic
        logger.info("On enable has been called")
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

        if (isPluginEnabled("ModelEngine"))
            registerEvents(ModelEngineSystem)

        //Register commands
        MobzyCommands()

        if (isPluginEnabled("Looty"))
            registerService<SerializablePrefabItemService>(MobzySerializablePrefabItemService)

        val config = MobzyConfigImpl()
        registerService<MobzyConfig>(config)

        gearyAddon {
            systems(
                WalkingAnimationSystem(),
                PathfinderAttachSystem(),
            )

            //Component event listeners
            MobzyNMSTypeInjector.track()
            CopyNBTSystem().track()

            autoscanComponents()
            autoscanConditions()

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

    override fun onDisable() { // Plugin shutdown logic
        super.onDisable()
        logger.info("onDisable has been invoked!")
        server.scheduler.cancelTasks(this)
    }
}
