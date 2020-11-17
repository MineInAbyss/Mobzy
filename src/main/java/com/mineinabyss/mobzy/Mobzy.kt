package com.mineinabyss.mobzy

import com.mineinabyss.geary.minecraft.store.BukkitEntityAccess
import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.plugin.registerEvents
import com.mineinabyss.mobzy.api.registerAddonWithMobzy
import com.mineinabyss.mobzy.ecs.listeners.MobzyECSListener
import com.mineinabyss.mobzy.ecs.listeners.PlayerJoinLeaveListener
import com.mineinabyss.mobzy.listener.MobListener
import com.mineinabyss.mobzy.registration.*
import com.mineinabyss.mobzy.spawning.SpawnTask
import org.bukkit.Bukkit
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

    @ExperimentalCommandDSL
    @ExperimentalTime
    override fun onEnable() {
        //Plugin startup logic
        logger.info("On enable has been called")
        saveDefaultConfig()
        reloadConfig()

        attachToGeary()

        MobzyPacketInterception.registerPacketInterceptors()
        MobzyTypeRegistry //instantiate singleton
        SpawnTask.startTask()


        //Register events
        registerEvents(
                MobListener,
                MobzyECSListener,
                PlayerJoinLeaveListener,
                BukkitEntityAccess,
        )

        //Register commands
        MobzyCommands

        //Register all players with teh ECS
        Bukkit.getOnlinePlayers().forEach { player ->
            BukkitEntityAccess.registerPlayer(player)
        }
        registerAddonWithMobzy()
    }

    override fun onDisable() { // Plugin shutdown logic
        super.onDisable()
        logger.info("onDisable has been invoked!")
        server.scheduler.cancelTasks(this)
    }
}
