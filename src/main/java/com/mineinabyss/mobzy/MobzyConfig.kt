package com.mineinabyss.mobzy

import com.charleskorn.kaml.Yaml
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.mobzy.api.spawnEntity
import com.mineinabyss.mobzy.api.toEntityTypesViaScoreboardTags
import com.mineinabyss.mobzy.configuration.MobConfiguration
import com.mineinabyss.mobzy.configuration.SpawnConfiguration
import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.registration.MobzyTemplates
import com.mineinabyss.mobzy.spawning.SpawnRegistry.unregisterSpawns
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.server.v1_15_R1.EntityLiving
import net.minecraft.server.v1_15_R1.EnumCreatureType
import net.minecraft.server.v1_15_R1.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import java.util.*

/**
 * @property registeredAddons A list of [MobzyAddon]s that have been registered with the plugin.
 * @property spawnCfgs A list of [FileConfiguration]s used for defining mob spawning behaviour.
 * @property mobCfgs A list of [FileConfiguration]s used for defining mob attributes, such as drops.
 * @property creatureTypes A list of the types of creatures (currently everything from [EnumCreatureType].
 * @property debug whether the plugin is in a debug state (used primarily for broadcasting messages)
 * @property spawnSearchRadius the radius around which players will count mobs towards the local mob cap
 * @property minChunkSpawnRad the minimum number of chunks away from the player in which a mob can spawn
 * @property maxChunkSpawnRad the maximum number of chunks away from the player in which a mob can spawn
 * @property maxCommandSpawns the maximum number of mobs to spawn with /mobzy spawn
 * @property spawnTaskDelay the delay in ticks between each attempted mob spawn
 * @property doMobSpawns whether custom mob spawning enabled
 */
@Serializable
data class MobzyConfig(
        var debug: Boolean,
        var doMobSpawns: Boolean,
//        var spawnSearchRadius: Double = 0.0,
        var minChunkSpawnRad: Int = 0,
        var maxChunkSpawnRad: Int = 0,
        var maxCommandSpawns: Int = 0,
        var spawnTaskDelay: Long = 0L,
        private val mobCaps: MutableMap<String, Int> = HashMap()
) {
    @Transient
    val creatureTypes: List<String> = listOf("MONSTER", "CREATURE", "AMBIENT", "WATER_CREATURE", "MISC")

    @Transient
    val registeredAddons: MutableList<MobzyAddon> = mutableListOf()

    @Transient
    val spawnCfgs: MutableList<SpawnConfiguration> = mutableListOf()

    @Transient
    val mobCfgs: MutableList<MobConfiguration> = mutableListOf()

    fun saveConfig() {
        mobzy.config.loadFromString(Yaml.default.stringify(MobzyConfig.serializer(), this))
        mobzy.saveConfig()
        spawnCfgs.forEach { it.save() }
    }

    init {
        //first tick only finishes when all plugins are loaded, which is when we activate addons
        Bukkit.getServer().scheduler.runTaskLater(mobzy, Runnable {
            mobzyConfig.activateAddons()
        }, 1L)
    }

    /**
     * @param creatureType The name of the [EnumCreatureType].
     * @return The mob cap for that mob in config.
     */
    fun getMobCap(creatureType: String): Int = mobCaps[creatureType]
            ?: error("could not find mob cap for $creatureType")

    /** Reload the configurations stored in the plugin. Most stuff requires a full reload of the plugin now */
    fun reload(sender: CommandSender = mobzy.server.consoleSender) {
        val consoleSender = mobzy.server.consoleSender
        fun attempt(success: String, fail: String, block: () -> Unit) {
            try {
                block()
                sender.success(success)
                if (sender != consoleSender) consoleSender.success(success)
            } catch (e: Exception) {
                sender.error(fail)
                if (sender != consoleSender) consoleSender.error(success)
                e.printStackTrace()
                return
            }
        }

        logSuccess("Reloading mobzy config")

        //We don't clear MobzyTypes since those will only ever change if an addon's code was changed which is impossible
        // to see during a soft reload like this.
        MobzyTemplates.clear()
        spawnCfgs.clear()
        mobCfgs.clear()
        unregisterSpawns()

        attempt("Reactivated all addonsMobzy", "Failed to reactive addons") {
            activateAddons()
        }

        attempt("Reloaded custom entities", "Failed to reload custom entities") {
            reloadExistingEntities()
        }

        sender.success("Successfully reloaded config")
    }


    /**
     * Addons have registered themselves with the plugin at this point. We just need to parse their configs
     * and create everything they need for them.
     */
    internal fun activateAddons() {
        registeredAddons.forEach { loadMobCfg(it) }
        registeredAddons.forEach { it.initializeMobs() }
        registeredAddons.forEach { loadSpawnCfg(it) }

        MobzyTemplates.loadTemplatesFromConfig()
        mobzy.registerSpawnTask()

        logSuccess("Registered addons: $registeredAddons")
    }

    /**
     * Loads a [SpawnConfiguration] for an addon
     *
     * @param plugin the addon registering it
     */
    fun loadSpawnCfg(plugin: MobzyAddon) {
        val spawnCfg = SpawnConfiguration(plugin.spawnConfig, plugin)
        spawnCfgs += spawnCfg
//        SpawnRegistry += spawnCfg
    }

    /**
     * Loads a [MobConfiguration] for an addon
     *
     * @param plugin the addon registering it
     */
    fun loadMobCfg(plugin: MobzyAddon) {
        val mobCfg = MobConfiguration(plugin.mobConfig, plugin)
        mobCfgs += mobCfg
    }

    /**
     * Every loaded custom entity in the world stops relating to the CustomMob class heirarchy after reload, so we
     * can't do something like customEntity instanceof CustomMob. Therefore, we "reload" those entities by deleting
     * them and copying their NBT data to new entities
     */
    private fun reloadExistingEntities() {
        var num = 0
        Bukkit.getServer().worlds.forEach { world ->
            world.entities.filter {
                if (it.scoreboardTags.contains("additionalPart")) it.remove().also { return@filter false }
                it.scoreboardTags.contains("customMob3") && it.toNMS() !is CustomMob
            }.forEach {
                val replacement = it.location.spawnEntity(it.toEntityTypesViaScoreboardTags())!!.toNMS<EntityLiving>()
                val nbt = NBTTagCompound()
                it.toNMS<EntityLiving>().b(nbt) //.b copies over the entity's nbt data to the compound
                it.remove()
                replacement.a(nbt) //.a copies the nbt data to the new entity
                num++
            }
        }
        logSuccess("Reloaded $num custom entities")
    }

    companion object {
        fun load(config: String) = Yaml.default.parse(MobzyConfig.serializer(), config)
    }
}