package com.mineinabyss.mobzy

import com.mineinabyss.idofront.messaging.*
import com.mineinabyss.mobzy.MobzyConfig.creatureTypes
import com.mineinabyss.mobzy.MobzyConfig.doMobSpawns
import com.mineinabyss.mobzy.MobzyConfig.isDebug
import com.mineinabyss.mobzy.MobzyConfig.maxChunkSpawnRad
import com.mineinabyss.mobzy.MobzyConfig.maxSpawnAmount
import com.mineinabyss.mobzy.MobzyConfig.minChunkSpawnRad
import com.mineinabyss.mobzy.MobzyConfig.mobCfgs
import com.mineinabyss.mobzy.MobzyConfig.registeredAddons
import com.mineinabyss.mobzy.MobzyConfig.spawnCfgs
import com.mineinabyss.mobzy.MobzyConfig.spawnSearchRadius
import com.mineinabyss.mobzy.MobzyConfig.spawnTaskDelay
import com.mineinabyss.mobzy.registration.MobzyTemplates
import com.mineinabyss.mobzy.spawning.SpawnRegistry.readCfg
import com.mineinabyss.mobzy.spawning.SpawnRegistry.unregisterSpawns
import net.minecraft.server.v1_15_R1.EnumCreatureType
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.util.*

/**
 * @property registeredAddons A list of [MobzyAddon]s that have been registered with the plugin.
 * @property spawnCfgs A list of [FileConfiguration]s used for defining mob spawning behaviour.
 * @property mobCfgs A list of [FileConfiguration]s used for defining mob attributes, such as drops.
 * @property creatureTypes A list of the types of creatures (currently everything from [EnumCreatureType].
 * @property isDebug whether the plugin is in a debug state (used primarily for broadcasting messages)
 * @property spawnSearchRadius the radius around which players will count mobs towards the local mob cap
 * @property minChunkSpawnRad the minimum number of chunks away from the player in which a mob can spawn
 * @property maxChunkSpawnRad the maximum number of chunks away from the player in which a mob can spawn
 * @property maxSpawnAmount the maximum number of mobs to spawn with /mobzy spawn
 * @property spawnTaskDelay the delay in ticks between each attempted mob spawn
 * @property doMobSpawns whether custom mob spawning enabled
 */
object MobzyConfig {
    val registeredAddons: MutableList<MobzyAddon> = mutableListOf()
    val spawnCfgs: MutableMap<File, FileConfiguration> = HashMap()
    val mobCfgs: MutableMap<File, FileConfiguration> = HashMap()
    val creatureTypes: List<String> = listOf("MONSTER", "CREATURE", "AMBIENT", "WATER_CREATURE", "MISC").also { logInfo("loaded mobzy config") }

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
    fun getMobCap(creatureType: String): Int = mobCaps[creatureType]
            ?: error("could not find mob cap for $creatureType")

    /** Reads the configuration values from the plugin's config.yml file */
    private fun loadConfigValues() {
        with(mobzy.config) {
            isDebug = getBoolean("debug")
            spawnSearchRadius = (get("spawnSearchRadius") as Number).toDouble()
            minChunkSpawnRad = getInt("minChunkSpawnRad")
            maxChunkSpawnRad = getInt("maxChunkSpawnRad")
            maxSpawnAmount = getInt("maxSpawnAmount")
            spawnTaskDelay = (get("spawnTaskDelay") as Number).toLong()
            creatureTypes.forEach { type -> mobCaps[type] = this["mobCaps.$type"] as Int } //register mob caps
            doMobSpawns = getBoolean("doMobSpawns") //this needs to be last since changing it will register spawn task with spawnTaskDelay
        }
    }

    /**
     * Registers a [FileConfiguration] of spawns to be used by the plugin.
     *
     * @param file   the file to be read from
     * @param plugin the plugin this file corresponds to
     */
    fun registerSpawnCfg(file: File, plugin: MobzyAddon) {
        registerCfg(spawnCfgs, file, plugin)
        readCfg(spawnCfgs[file]!!)
    }

    /**
     * Registers a [FileConfiguration] of mobs, describing their attributes, such as model to be used
     *
     * @param file   the file to be read from
     * @param plugin the plugin this file corresponds to
     */
    fun registerMobCfg(file: File, plugin: MobzyAddon) {
        registerCfg(mobCfgs, file, plugin)
        if (!registeredAddons.contains(plugin)) registeredAddons.add(plugin)
        logSuccess("Registered addons: $registeredAddons")
    }

    private fun registerCfg(config: MutableMap<File, FileConfiguration>, file: File, plugin: MobzyAddon) {
        logInfo("Registering configuration ${file.name}")
        if (!plugin.dataFolder.exists()) plugin.dataFolder.mkdir()
        if (!file.exists()) {
            file.parentFile.mkdirs()
            plugin.saveResource(file.name, false)
            plugin.logger.info(ChatColor.GREEN.toString() + file.name + " has been created")
        }
        val configuration: FileConfiguration = YamlConfiguration.loadConfiguration(file)
        config[file] = configuration
        logSuccess("Registered configuration: ${file.name}")
    }

    fun saveSpawnCfg(config: FileConfiguration) {
        val configEntry = spawnCfgs.entries.first { it.value == config }
        try {
            configEntry.value.save(configEntry.key)
            logSuccess("Spawns file has been saved")
        } catch (e: IOException) {
            logError("Could not save the spawns file")
            e.printStackTrace()
        }
    }

    /** Reload the configurations stored in the plugin. Most stuff requires a full reload of the plugin now */
    fun reload(sender: CommandSender = mobzy.server.consoleSender) {
        val consoleSender = mobzy.server.consoleSender
        fun attempt(success: String, fail: String, block: () -> Unit) {
            try {
                block()
                sender.success(success)
                if(sender != consoleSender) consoleSender.success(success)
            } catch (e: Exception) {
                sender.error(fail)
                if(sender != consoleSender) consoleSender.error(success)
                e.printStackTrace()
                return
            }
        }

        logSuccess("Reloading mobzy config")

        //We don't clear MobzyTypes since those will only ever change if an addon's code was changed which is impossible
        // to see during a soft reload like this.
        MobzyTemplates.clear()
        unregisterSpawns()

        logSuccess("Registered addons: $registeredAddons")

        attempt("Registered addons with Mobzy", "Failed to register addons with Mobzy") {
            registeredAddons.forEach { it.registerWithMobzy() }
        }

        attempt("Loaded config values for Mobzy", "Failed to load config values Mobzy") { loadConfigValues() }

        sender.success("Reloaded config")
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
}