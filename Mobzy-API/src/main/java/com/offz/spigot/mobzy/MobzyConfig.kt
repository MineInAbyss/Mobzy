package com.offz.spigot.mobzy

import com.mineinabyss.idofront.messaging.logError
import com.mineinabyss.idofront.messaging.logInfo
import com.offz.spigot.mobzy.spawning.SpawnRegistry.readCfg
import com.offz.spigot.mobzy.spawning.SpawnRegistry.unregisterAll
import net.minecraft.server.v1_15_R1.EnumCreatureType
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.util.*

class MobzyConfig {
    val registeredAddons: MutableList<MobzyAddon> = mutableListOf()
    val spawnCfgs: MutableMap<File, FileConfiguration> = HashMap()
    val mobCfgs: MutableMap<File, FileConfiguration> = HashMap()
    val creatureTypes: List<String> = listOf("MONSTER", "CREATURE", "AMBIENT", "WATER_CREATURE", "MISC")

    /**
     * Reads the configuration values from the plugin's config.yml file
     */
    private fun loadConfigValues() {
        val config = mobzy.config
        isDebug = config.getBoolean("debug")
        doMobSpawns = config.getBoolean("doMobSpawns")
        spawnSearchRadius = (config.get("spawnSearchRadius") as Number).toDouble()
        minChunkSpawnRad = config.getInt("minChunkSpawnRad")
        maxChunkSpawnRad = config.getInt("maxChunkSpawnRad")
        maxSpawnAmount = config.getInt("maxSpawnAmount")
        spawnTaskDelay = (config.get("spawnTaskDelay") as Number).toLong()
        creatureTypes.forEach { type -> mobCaps[type] = config["mobCaps.$type"] as Int } //register mob caps
    }

    /**
     * Registers a [FileConfiguration] of spawns to be used by the plugin.
     *
     * @param file   the file to be read from
     * @param plugin the plugin this file corresponds to
     */
    fun registerSpawnCfg(file: File, plugin: JavaPlugin) {
        registerCfg(spawnCfgs, file, plugin)
        readCfg(spawnCfgs[file]!!)
    }

    /**
     * Registers a [FileConfiguration] of mobs, describing their attributes, such as model to be used
     *
     * @param file   the file to be read from
     * @param plugin the plugin this file corresponds to
     */
    fun registerMobCfg(file: File, plugin: JavaPlugin) {
        if (plugin !is MobzyAddon) error("Cannot register $plugin, it is not a MobzyAddon")
        registerCfg(mobCfgs, file, plugin)
        if (!registeredAddons.contains(plugin)) registeredAddons.add(plugin as MobzyAddon)
        logInfo("Registered addons: $registeredAddons")
    }

    private fun registerCfg(config: MutableMap<File, FileConfiguration>, file: File, plugin: JavaPlugin) {
        logInfo("Registering configuration ${file.name}")
        if (!plugin.dataFolder.exists()) plugin.dataFolder.mkdir()
        if (!file.exists()) {
            file.parentFile.mkdirs()
            plugin.saveResource(file.name, false)
            plugin.logger.info(ChatColor.GREEN.toString() + file.name + " has been created")
        }
        val configuration: FileConfiguration = YamlConfiguration.loadConfiguration(file)
        config[file] = configuration
        logInfo("Registered configuration: $configuration")
    }

    fun saveSpawnCfg(config: FileConfiguration) {
        val configEntry = spawnCfgs.entries.first { it.value == config }
        try {
            configEntry.value.save(configEntry.key)
            logInfo("Spawns file has been saved")
        } catch (e: IOException) {
            logError("Could not save the spawns file")
            e.printStackTrace()
        }
    }

    /**
     * Reload the configurations stored in the plugin. Most stuff requires a full reload of the plugin now
     */
    fun reload() {
        mobzy.customTypes.reload()
        logInfo("Registered addons: $registeredAddons")
        registeredAddons.forEach { it.registerWithMobzy(mobzy) }
        loadConfigValues()
        reloadConfigurationMap(mobCfgs)
        unregisterAll()
        reloadConfigurationMap(spawnCfgs)
        mobzy.registerSpawnTask()
    }

    /**
     * @param configs reload the [FileConfiguration]s of this passed map
     */
    private fun reloadConfigurationMap(configs: MutableMap<File, FileConfiguration>) {
        for (file in configs.keys) {
            val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)
            configs[file] = config
            readCfg(config)
        }
    }

    /**
     * @property isDebug whether the plugin is in a debug state (used primarily for broadcasting messages)
     * @property spawnSearchRadius the radius around which players will count mobs towards the local mob cap
     * @property minChunkSpawnRad the minimum number of chunks away from the player in which a mob can spawn
     * @property maxChunkSpawnRad the maximum number of chunks away from the player in which a mob can spawn
     * @property maxSpawnAmount the maximum number of mobs to spawn with /mobzy spawn
     * @property spawnTaskDelay the delay in ticks between each attempted mob spawn
     * @property doMobSpawns whether custom mob spawning enabled
     */
    companion object {
        var isDebug = false; private set
        var doMobSpawns = false
            set(enabled) {
                if (enabled && !doMobSpawns) {
                    field = true
                    mobzy.config.set("doMobSpawns", true)
                    mobzy.saveConfig()
                    mobzy.registerSpawnTask()
                } else if (!enabled && doMobSpawns) {
                    field = false
                    mobzy.config.set("doMobSpawns", false)
                    mobzy.saveConfig()
                    mobzy.registerSpawnTask()
                }
            }
        var spawnSearchRadius = 0.0; private set
        var minChunkSpawnRad = 0; private set
        var maxChunkSpawnRad = 0; private set
        var maxSpawnAmount = 0; private set
        var spawnTaskDelay = 0L; private set
        private val mobCaps: MutableMap<String, Int> = HashMap()

        /**
         * @param creatureType The name of the [EnumCreatureType].
         * @return The mob cap for that mob in config.
         */
        fun getMobCap(creatureType: String): Int =
                mobCaps[creatureType] ?: error("could not find mob cap for $creatureType")

    }
}