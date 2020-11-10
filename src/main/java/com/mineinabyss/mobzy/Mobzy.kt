package com.mineinabyss.mobzy

import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.EngineImpl
import com.mineinabyss.geary.ecs.systems.PlayerJoinLeaveListener
import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.plugin.registerEvents
import com.mineinabyss.idofront.plugin.registerService
import com.mineinabyss.looty.ecs.LootyCommands
import com.mineinabyss.looty.ecs.config.LootyAddon
import com.mineinabyss.looty.ecs.config.registerAddonWithLooty
import com.mineinabyss.looty.ecs.systems.ItemTrackerSystem
import com.mineinabyss.mobzy.api.registerAddonWithMobzy
import com.mineinabyss.mobzy.ecs.BukkitEntityAccess
import com.mineinabyss.mobzy.listener.MobListener
import com.mineinabyss.mobzy.registration.MobzyECSRegistry
import com.mineinabyss.mobzy.registration.MobzyPacketInterception
import com.mineinabyss.mobzy.registration.MobzyTypeRegistry
import com.mineinabyss.mobzy.registration.MobzyWorldguard
import com.mineinabyss.mobzy.spawning.SpawnTask
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.time.ExperimentalTime

/** Gets [Mobzy] via Bukkit once, then sends that reference back afterwards */
val mobzy: Mobzy by lazy { JavaPlugin.getPlugin(Mobzy::class.java) }

class Mobzy : JavaPlugin(), MobzyAddon, LootyAddon {

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

        registerService<Engine>(EngineImpl())

        MobzyECSRegistry.register()
        MobzyPacketInterception.registerPacketInterceptors()
        MobzyTypeRegistry //instantiate singleton
        SpawnTask.startTask()


        //Register events
        registerEvents(
                MobListener,
                MobzyECSRegistry,
                ItemTrackerSystem,
                PlayerJoinLeaveListener,
                BukkitEntityAccess,
        )

        //Register commands
        MobzyCommands
        LootyCommands

        //Register all players with teh ECS
        Bukkit.getOnlinePlayers().forEach { player ->
            BukkitEntityAccess.registerPlayer(player)
        }

        registerAddonWithMobzy()
        registerAddonWithLooty()
    }

    override val mobConfigDir = File(dataFolder, "mobs")
    override val relicsDir = File(dataFolder, "relics")
    override val spawnConfig = File(dataFolder, "spawns.yml")

    override fun onDisable() { // Plugin shutdown logic
        super.onDisable()
        logger.info("onDisable has been invoked!")
        server.scheduler.cancelTasks(this)
    }
}