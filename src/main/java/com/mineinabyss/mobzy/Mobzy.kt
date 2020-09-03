package com.mineinabyss.mobzy

import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.plugin.registerEvents
import com.mineinabyss.mobzy.listener.MobListener
import com.mineinabyss.mobzy.registration.MobzyECSRegistry
import com.mineinabyss.mobzy.registration.MobzyPacketInterception
import com.mineinabyss.mobzy.registration.MobzyTypeRegistry
import com.mineinabyss.mobzy.registration.MobzyWorldguard
import com.mineinabyss.mobzy.spawning.SpawnTask
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
    @ExperimentalTime
    override fun onEnable() {
        //Plugin startup logic
        logger.info("On enable has been called")
        saveDefaultConfig()
        reloadConfig()

        MobzyECSRegistry.register()
        MobzyPacketInterception.registerPacketInterceptors()
        MobzyTypeRegistry //instantiate singleton
        SpawnTask.startTask()

        //Register events
        registerEvents(
                MobListener,
                MobzyECSRegistry
        )

        //Register commands
        MobzyCommands
    }

    override fun onDisable() { // Plugin shutdown logic
        super.onDisable()
        logger.info("onDisable has been invoked!")
        server.scheduler.cancelTasks(this)
    }
}