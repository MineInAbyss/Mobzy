package com.mineinabyss.mobzy

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.derongan.minecraft.guiy.GuiListener
import com.mineinabyss.mobzy.api.isCustomMob
import com.mineinabyss.mobzy.listener.MobListener
import com.mineinabyss.mobzy.registration.MobzyTypes
import com.mineinabyss.mobzy.spawning.SpawnTask
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.StringFlag
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

/** Gets [Mobzy] via Bukkit once, then sends that reference back afterwards */
val mobzy: Mobzy by lazy { JavaPlugin.getPlugin(Mobzy::class.java) }

/** Mobzy's configuration information */
val mobzyConfig: MobzyConfig get() = mobzy.mobzyConfig

class Mobzy : JavaPlugin() {
    lateinit var mobzyConfig: MobzyConfig

    override fun onLoad() {
        logger.info("On load has been called")

        //TODO try to allow plugin spawning in WorldGuard's config automatically (see if this worked)
        //onCreatureSpawn in WorldGuardEntityListener throws errors if we don't enable custom entity spawns
//        WorldGuard.getInstance().platform.globalStateManager.get(BukkitAdapter.adapt(server.worlds.first()))
//                .blockPluginSpawning = false

        //Registering custom WorldGuard flag
        val registry = WorldGuard.getInstance().flagRegistry
        val mzSpawnRegions = registry["cm-spawns"]
        val mzSpawnOverlap = registry["mz-spawn-overlap"]
        //register MZ_SPAWN_REGIONS
        if (mzSpawnRegions is StringFlag) //avoid problems if registering flag that already exists
            MZ_SPAWN_REGIONS = mzSpawnRegions
        else try {
            val flag = StringFlag("cm-spawns", "")
            registry.register(flag)
            MZ_SPAWN_REGIONS = flag
        } catch (e: FlagConflictException) {
            e.printStackTrace()
        }
        //register MZ_SPAWN_OVERLAP
        if (mzSpawnOverlap is StringFlag) MZ_SPAWN_OVERLAP = mzSpawnOverlap
        else try {
            val flag = StringFlag("mz-spawn-overlap", "stack")
            registry.register(flag)
            MZ_SPAWN_OVERLAP = flag
        } catch (e: FlagConflictException) {
            e.printStackTrace()
        }

        //register protocol manager
        val protocolManager = ProtocolLibrary.getProtocolManager()!!

        protocolManager.addPacketListener(object : PacketAdapter(this, ListenerPriority.NORMAL,
                PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
            override fun onPacketSending(event: PacketEvent) { // Item packets (id: 0x29)
                if (event.packetType == PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
                    if (Bukkit.getEntity(event.packet.uuiDs.read(0))?.isCustomMob == true)
                        event.packet.integers.write(1, 95)
                }
            }
        })
    }

    override fun onEnable() {
        //Plugin startup logic
        logger.info("On enable has been called")
        saveDefaultConfig()
        reloadConfig()
        MobzyTypes
        mobzyConfig = MobzyConfig.load(config.saveToString())

        //Register events
        server.pluginManager.registerEvents(MobListener, this)
        server.pluginManager.registerEvents(GuiListener(this), this)

        //Register commands
        MobzyCommands
    }

    private var currentTask: SpawnTask? = null

    fun registerSpawnTask() {
        if (mobzyConfig.doMobSpawns) {
            if(currentTask == null) {
                currentTask = SpawnTask()
                currentTask?.runTaskTimer(this, 0, mobzyConfig.spawnTaskDelay)
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

    companion object {
        //TODO Make these into their own custom flags instead of StringFlag
        //TODO rename this to MZ_... in the WorldGuard config files :mittysweat:
        @JvmStatic
        var MZ_SPAWN_REGIONS: StringFlag? = null

        @JvmStatic
        var MZ_SPAWN_OVERLAP: StringFlag? = null
    }
}