package com.mineinabyss.mobzy

import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.mobzy.listener.MobListener
import com.mineinabyss.mobzy.registration.MobzyECSRegistry
import com.mineinabyss.mobzy.registration.MobzyPacketInterception
import com.mineinabyss.mobzy.registration.MobzyTypeRegistry
import com.mineinabyss.mobzy.registration.MobzyWorldguard
import com.mineinabyss.mobzy.spawning.SpawnTask
import kotlinx.serialization.ImplicitReflectionSerializer
import org.bukkit.plugin.java.JavaPlugin
import kotlin.time.ExperimentalTime

/** Gets [Mobzy] via Bukkit once, then sends that reference back afterwards */
val mobzy: Mobzy by lazy { JavaPlugin.getPlugin(Mobzy::class.java) }

class Mobzy : JavaPlugin() {

    override fun onLoad() {
        logger.info("On load has been called")

        MobzyWorldguard.registerFlags()
    }

    @ExperimentalCommandDSL
    @ImplicitReflectionSerializer
    @ExperimentalTime
    override fun onEnable() {
        //Plugin startup logic
        logger.info("On enable has been called")
        saveDefaultConfig()
        reloadConfig()

        MobzyECSRegistry.register()
        MobzyPacketInterception.registerPacketInterceptors()
        MobzyTypeRegistry //TODO more specific name

        //Register events
        server.pluginManager.registerEvents(MobListener, this)
        server.pluginManager.registerEvents(MobzyECSRegistry, this)

        //Register commands
        MobzyCommands
    }

    private var currentTask: SpawnTask? = null

    fun registerSpawnTask() {
        if (MobzyConfig.doMobSpawns) {
            if (currentTask == null) {
                currentTask = SpawnTask()
                currentTask?.runTaskTimer(this, 0, MobzyConfig.spawnTaskDelay)
            }
        } else {
            currentTask?.cancel()
            currentTask = null
        }
    }

    override fun onDisable() { // Plugin shutdown logic
        super.onDisable()
        logger.info("onDisable has been invoked!")
        server.scheduler.cancelTasks(this)
    }
}