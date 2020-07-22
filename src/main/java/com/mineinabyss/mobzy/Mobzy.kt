package com.mineinabyss.mobzy

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.mineinabyss.mobzy.api.isCustomMob
import com.mineinabyss.mobzy.listener.MobListener
import com.mineinabyss.mobzy.registration.MobzyTypes
import com.mineinabyss.mobzy.spawning.SpawnTask
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.StringFlag
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import com.comphenix.protocol.PacketType.Play.Server as Packets

/** Gets [Mobzy] via Bukkit once, then sends that reference back afterwards */
val mobzy: Mobzy by lazy { JavaPlugin.getPlugin(Mobzy::class.java) }

class Mobzy : JavaPlugin() {

    override fun onLoad() {
        logger.info("On load has been called")

        //TODO try to allow plugin spawning in WorldGuard's config automatically
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

        //send zombie as entity type for custom mobs
        protocolManager.addPacketListener(object : PacketAdapter(this, ListenerPriority.NORMAL,
                Packets.SPAWN_ENTITY_LIVING) {
            override fun onPacketSending(event: PacketEvent) {
                if (Bukkit.getEntity(event.packet.uuiDs.read(0))?.isCustomMob == true)
                    event.packet.integers.write(1, 101)
            }
        })

        //pitch lock custom mobs
        protocolManager.addPacketListener(object : PacketAdapter(this, ListenerPriority.NORMAL,
                //all these packets seem to be enough to cover all head rotations
                Packets.ENTITY_LOOK,
                Packets.REL_ENTITY_MOVE_LOOK,
                Packets.LOOK_AT,
                Packets.ENTITY_TELEPORT
        ) {
            override fun onPacketSending(event: PacketEvent) {
                if (event.packet.getEntityModifier(event).read(0).isCustomMob) //check entity involved
                    event.packet.bytes.write(1, 0) //modify pitch to be zero
            }
        })
    }

    override fun onEnable() {
        //Plugin startup logic
        logger.info("On enable has been called")
        saveDefaultConfig()
        reloadConfig()
        MobzyTypes

        //Register events
        server.pluginManager.registerEvents(MobListener, this)
//        server.pluginManager.registerEvents(GuiListener(this), this)

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

    companion object {
        //TODO Make these into their own custom flags instead of StringFlag
        //TODO rename this to MZ_... in the WorldGuard config files :mittysweat:
        @JvmStatic
        var MZ_SPAWN_REGIONS: StringFlag? = null

        @JvmStatic
        var MZ_SPAWN_OVERLAP: StringFlag? = null
    }
}