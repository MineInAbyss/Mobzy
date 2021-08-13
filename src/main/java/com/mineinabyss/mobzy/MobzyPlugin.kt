package com.mineinabyss.mobzy

import com.mineinabyss.geary.minecraft.dsl.GearyLoadPhase
import com.mineinabyss.geary.minecraft.dsl.attachToGeary
import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.plugin.isPluginEnabled
import com.mineinabyss.idofront.plugin.registerEvents
import com.mineinabyss.idofront.slimjar.LibraryLoaderInjector
import com.mineinabyss.mobzy.api.registerAddonWithMobzy
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.ecs.events.MobzyEventListener
import com.mineinabyss.mobzy.ecs.listeners.MobzyECSListener
import com.mineinabyss.mobzy.ecs.systems.CopyNBTSystem
import com.mineinabyss.mobzy.ecs.systems.ModelEngineSystem
import com.mineinabyss.mobzy.ecs.systems.WalkingAnimationSystem
import com.mineinabyss.mobzy.listener.GearySpawningListener
import com.mineinabyss.mobzy.listener.MobListener
import com.mineinabyss.mobzy.registration.MobzyNMSTypeInjector
import com.mineinabyss.mobzy.registration.MobzyPacketInterception
import com.mineinabyss.mobzy.registration.MobzyWorldguard
import com.mineinabyss.mobzy.spawning.MobCountManager
import kotlinx.serialization.InternalSerializationApi
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File
import kotlin.time.ExperimentalTime

/** Gets [MobzyPlugin] via Bukkit once, then sends that reference back afterwards */
val mobzy: JavaPlugin by lazy { JavaPlugin.getPlugin(MobzyPlugin::class.java) }

class MobzyPlugin : JavaPlugin, MobzyAddon {
    constructor() : super()
    protected constructor(
        loader: JavaPluginLoader,
        description: PluginDescriptionFile,
        dataFolder: File,
        file: File
    ) : super(loader, description, dataFolder, file)

    override val mobConfigDir = File(dataFolder, "mobs")
    override val spawnConfig = File(dataFolder, "spawns.yml")

    override fun onLoad() {
        logger.info("On load has been called")

        MobzyWorldguard.registerFlags()
    }

    @InternalSerializationApi
    @ExperimentalCommandDSL
    @ExperimentalTime
    override fun onEnable() {
        LibraryLoaderInjector.inject(this)

        //Plugin startup logic
        logger.info("On enable has been called")
        saveDefaultConfig()
        reloadConfig()

        //Register events
        registerEvents(
            MobListener,
            MobzyECSListener,
            MobzyEventListener,
            MobCountManager,
            GearySpawningListener,
        )

        if (isPluginEnabled("ModelEngine"))
            registerEvents(ModelEngineSystem)

        //Register commands
        MobzyCommands()

        registerAddonWithMobzy()

        attachToGeary {
            systems(
                WalkingAnimationSystem,
                CopyNBTSystem(),
                MobzyNMSTypeInjector,
            )

            autoscanComponents()
            autoscanConditions()

            // Autoscan the subclasses of PathfinderComponent
            autoscan<PathfinderComponent>()

            loadPrefabs(mobConfigDir)

            startup {
                GearyLoadPhase.ENABLE {
                    MobzyPacketInterception.registerPacketInterceptors()

                    MobzyConfig.load()
                }
            }
        }
    }

    override fun onDisable() { // Plugin shutdown logic
        super.onDisable()
        logger.info("onDisable has been invoked!")
        server.scheduler.cancelTasks(this)
    }
}
