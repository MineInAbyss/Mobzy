package com.mineinabyss.mobzy

import com.mineinabyss.geary.minecraft.dsl.attachToGeary
import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.plugin.registerEvents
import com.mineinabyss.mobzy.api.registerAddonWithMobzy
import com.mineinabyss.mobzy.api.toMobzy
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.ecs.events.MobzyEventListener
import com.mineinabyss.mobzy.ecs.listeners.MobzyECSListener
import com.mineinabyss.mobzy.ecs.systems.WalkingAnimationSystem
import com.mineinabyss.mobzy.listener.MobListener
import com.mineinabyss.mobzy.registration.MobzyPacketInterception
import com.mineinabyss.mobzy.registration.MobzyTypeRegistry
import com.mineinabyss.mobzy.registration.MobzyTypes
import com.mineinabyss.mobzy.registration.MobzyWorldguard
import com.mineinabyss.mobzy.spawning.SpawnTask
import com.okkero.skedule.schedule
import kotlinx.serialization.InternalSerializationApi
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.time.ExperimentalTime

/** Gets [Mobzy] via Bukkit once, then sends that reference back afterwards */
val mobzy: Mobzy by lazy { JavaPlugin.getPlugin(Mobzy::class.java) }

class Mobzy : JavaPlugin(), MobzyAddon {
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
        //Plugin startup logic
        logger.info("On enable has been called")
        saveDefaultConfig()
        reloadConfig()

        attachToGeary(types = MobzyTypes) {
            systems(
                WalkingAnimationSystem
            )

            autoscanComponents()

            // Autoscan the subclasses of PathfinderComponent
            autoscan<PathfinderComponent>()

            bukkitEntityAccess {
                entityConversion { toMobzy() }
            }
        }

        MobzyPacketInterception.registerPacketInterceptors()
        MobzyTypeRegistry //instantiate singleton
        SpawnTask.startTask()

        //Register events
        registerEvents(
            MobListener,
            MobzyECSListener,
            MobzyEventListener
        )

        //Register commands
        MobzyCommands()

        registerAddonWithMobzy()

        //first tick only finishes when all plugins are loaded, which is when we activate addons
        mobzy.schedule {
            waitFor(1)
            MobzyConfig.activateAddons()
        }
    }

    override fun onDisable() { // Plugin shutdown logic
        super.onDisable()
        logger.info("onDisable has been invoked!")
        server.scheduler.cancelTasks(this)
    }
}
