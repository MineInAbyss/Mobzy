package com.mineinabyss.mobzy

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.derongan.minecraft.guiy.GuiListener
import com.mineinabyss.mobzy.listener.MobListener
import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.spawning.SpawnTask
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.StringFlag
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException
import net.minecraft.server.v1_15_R1.EntityLiving
import net.minecraft.server.v1_15_R1.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

/**
 * Gets [Mobzy] via bukkit once, then sends that reference back afterwards
 */
val mobzy: Mobzy by lazy { JavaPlugin.getPlugin(Mobzy::class.java) }

class Mobzy : JavaPlugin() {
    lateinit var mobzyConfig: MobzyConfig
        private set
    lateinit var mobzyTypes: MobzyType
        private set

    override fun onLoad() {
        logger.info("On load has been called")

        //TODO try to allow plugin spawning in WorldGuard's config automatically
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
        mobzyTypes = MobzyType()
        mobzyConfig = MobzyConfig()
        mobzyConfig.reload() //lots of startup logic in here

        //Register events
        server.pluginManager.registerEvents(MobListener(), this)
        server.pluginManager.registerEvents(GuiListener(this), this)

        //Reload existing addons
        /*getLogger().info("Reloading addons: " + getConfig().getStringList(REGISTERED_ADDONS_KEY));
        for (String name : getConfig().getStringList(REGISTERED_ADDONS_KEY)) {
            PluginManager pluginManager = Bukkit.getServer().getPluginManager();
            Plugin addon = pluginManager.getPlugin(name);
            if (addon instanceof MobzyAddon && pluginManager.isPluginEnabled(name) && !mobzyConfig.getRegisteredAddons().contains(addon))
                ((MobzyAddon) addon).registerWithMobzy(this);
        }*/

        //Register commands
        MobzyCommands()
    }

    private var spawnTaskID = -1

    fun registerSpawnTask() {
        server.scheduler.cancelTask(spawnTaskID)
        spawnTaskID = -1

        if (MobzyConfig.doMobSpawns) {
            val spawnTask: Runnable = SpawnTask()
            spawnTaskID = server.scheduler.scheduleSyncRepeatingTask(this, spawnTask, 0, MobzyConfig.spawnTaskDelay)
        }
    }

    /**
     * Every loaded custom entity in the world stops relating to the CustomMob class heirarchy after reload, so we
     * can't do something like customEntity instanceof CustomMob. Therefore, we "reload" those entities by deleting
     * them and copying their NBT data to new entities
     */
    fun reloadExistingEntities() {
        var num = 0
        Bukkit.getServer().worlds.forEach { world ->
            world.entities.filter {
                if (it.scoreboardTags.contains("additionalPart")) it.remove().also { return@filter false }
                it.scoreboardTags.contains("customMob2") && it.toNMS() !is CustomMob
            }.forEach {
                val replacement = it.location.spawnEntity((it.scoreboardTags).toEntityType())!!.toNMS<EntityLiving>()
                val nbt = NBTTagCompound()
                it.toNMS<EntityLiving>().b(nbt) //.b copies over the entity's nbt data to the compound
                it.remove()
                replacement.a(nbt) //.a copies the nbt data to the new entity
                num++
            }
        }
        logger.info(ChatColor.GREEN.toString() + "Reloaded " + num + " custom entities")
    }

    override fun onDisable() { // Plugin shutdown logic
        super.onDisable()
        logger.info("onDisable has been invoked!")
        server.scheduler.cancelTasks(this)
        //        Bukkit.broadcastMessage("Saving addons " + mobzyConfig.getRegisteredAddons().toString());
//        getConfig().set(REGISTERED_ADDONS_KEY, mobzyConfig.getRegisteredAddons().stream().map(addon -> ((Plugin) addon).getName()).collect(Collectors.toList()));
//        saveConfig();
    }

    companion object {
        private const val REGISTERED_ADDONS_KEY = "addons"

        //TODO Make these into their own custom flags instead of StringFlag
        //TODO rename this to MZ_... in the WorldGuard config files :mittysweat:
        @JvmStatic
        var MZ_SPAWN_REGIONS: StringFlag? = null

        @JvmStatic
        var MZ_SPAWN_OVERLAP: StringFlag? = null
    }
}