package com.offz.spigot.mobzy

import com.derongan.minecraft.guiy.GuiListener
import com.offz.spigot.mobzy.CustomType.Companion.getType
import com.offz.spigot.mobzy.CustomType.Companion.registerTypes
import com.offz.spigot.mobzy.CustomType.Companion.spawnEntity
import com.offz.spigot.mobzy.listener.MobListener
import com.offz.spigot.mobzy.mobs.CustomMob
import com.offz.spigot.mobzy.spawning.SpawnTask
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.StringFlag
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException
import net.minecraft.server.v1_15_R1.EntityLiving
import net.minecraft.server.v1_15_R1.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

lateinit var mobzy: Mobzy
    private set

class Mobzy : JavaPlugin() {
    lateinit var mobzyConfig: MobzyConfig
        private set
    private lateinit var context: MobzyContext

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
    }

    override fun onEnable() {
        mobzy = this
        logger.info("On enable has been called")
        saveDefaultConfig()
        mobzyConfig = MobzyConfig()
        reloadConfig()
        registerTypes() //not clean but mob ids need to be registered with the server on startup or the mobs get removed

        //Plugin startup logic
        context = MobzyContext(config, mobzyConfig) //Create new context and add plugin and logger to it
        context.plugin = this
        context.logger = logger

        //Register events
        server.pluginManager.registerEvents(MobListener(context), this)
        server.pluginManager.registerEvents(GuiListener(this), this)

        //Register repeating tasks
        if (MobzyConfig.doMobSpawns) {
            val spawnTask: Runnable = SpawnTask()
            server.scheduler.scheduleSyncRepeatingTask(this, spawnTask, 0, MobzyConfig.spawnTaskDelay)
        }

        //Reload existing addons
        /*getLogger().info("Reloading addons: " + getConfig().getStringList(REGISTERED_ADDONS_KEY));
        for (String name : getConfig().getStringList(REGISTERED_ADDONS_KEY)) {
            PluginManager pluginManager = Bukkit.getServer().getPluginManager();
            Plugin addon = pluginManager.getPlugin(name);
            if (addon instanceof MobzyAddon && pluginManager.isPluginEnabled(name) && !mobzyConfig.getRegisteredAddons().contains(addon))
                ((MobzyAddon) addon).registerWithMobzy(this);
        }*/

        val commandExecutor = MobzyCommands(context)
        getCommand("mobzy")!!.setExecutor(commandExecutor)
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
                val replacement = spawnEntity(getType(it.scoreboardTags), it.location)!!.toNMS<EntityLiving>()
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