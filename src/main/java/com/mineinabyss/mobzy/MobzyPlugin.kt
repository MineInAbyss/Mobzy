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
import com.mineinabyss.mobzy.ecs.listeners.MobzyECSListener
import com.mineinabyss.mobzy.ecs.systems.AddPrefabsListener
import com.mineinabyss.mobzy.ecs.systems.CopyNBTSystem
import com.mineinabyss.mobzy.ecs.systems.ModelEngineSystem
import com.mineinabyss.mobzy.ecs.systems.WalkingAnimationSystem
import com.mineinabyss.mobzy.listener.GearySpawningListener
import com.mineinabyss.mobzy.listener.MobListener
import com.mineinabyss.mobzy.registration.MobzyNMSTypeInjector
import com.mineinabyss.mobzy.registration.MobzyPacketInterception
import com.mineinabyss.mobzy.registration.MobzyWorldguard
import com.mineinabyss.mobzy.spawning.MobCountManager
import org.bukkit.plugin.java.JavaPlugin

/** Gets [MobzyPlugin] via Bukkit once, then sends that reference back afterwards */
val mobzy: JavaPlugin by lazy { JavaPlugin.getPlugin(MobzyPlugin::class.java) }

class MobzyPlugin : JavaPlugin() {

    override fun onLoad() {
        logger.info("On load has been called")
        MobzyWorldguard.registerFlags()
    }

    @ExperimentalCommandDSL
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

        gearyAddon {
            systems(
                WalkingAnimationSystem(),
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
                    MobzyConfig.load()
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
