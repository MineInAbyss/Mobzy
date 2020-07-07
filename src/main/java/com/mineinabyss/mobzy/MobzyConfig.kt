package com.mineinabyss.mobzy

import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.mobzy.api.spawnEntity
import com.mineinabyss.mobzy.api.toEntityTypesViaScoreboardTags
import com.mineinabyss.mobzy.configuration.MobConfig
import com.mineinabyss.mobzy.configuration.PluginConfig
import com.mineinabyss.mobzy.configuration.PluginConfigHolder
import com.mineinabyss.mobzy.configuration.ReloadContext
import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.registration.MobzyTemplates
import com.mineinabyss.mobzy.spawning.SpawnRegistry.unregisterSpawns
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_15_R1.EntityLiving
import net.minecraft.server.v1_15_R1.EnumCreatureType
import net.minecraft.server.v1_15_R1.NBTTagCompound
import org.bukkit.Bukkit


/**
 * @property debug whether the plugin is in a debug state (used primarily for broadcasting messages)
 * @property doMobSpawns whether custom mob spawning enabled
 * @property minChunkSpawnRad the minimum number of chunks away from the player in which a mob can spawn
 * @property maxChunkSpawnRad the maximum number of chunks away from the player in which a mob can spawn
 * @property maxCommandSpawns the maximum number of mobs to spawn with /mobzy spawn
 * @property playerGroupRadius the radius around which players will count mobs towards the local mob cap
 * @property spawnTaskDelay the delay in ticks between each attempted mob spawn
 * @property mobCaps A map of mobs to the maximum number of that mob to spawn per player online TODO this might actually be mob type caps
 */
interface IMobzyConfigData {
    var debug: Boolean
    var doMobSpawns: Boolean
    var minChunkSpawnRad: Int
    var maxChunkSpawnRad: Int
    var maxCommandSpawns: Int
    var playerGroupRadius: Double
    var spawnTaskDelay: Long
    var mobCaps: MutableMap<String, Int>
}

@Serializable
data class MobzyConfigData(
        override var debug: Boolean = false,
        override var doMobSpawns: Boolean = false,
        override var minChunkSpawnRad: Int = 3,
        override var maxChunkSpawnRad: Int = 7,
        override var maxCommandSpawns: Int = 50,
        override var playerGroupRadius: Double = 128.0,
        override var spawnTaskDelay: Long = 100,
        override var mobCaps: MutableMap<String, Int> = hashMapOf()
) : IMobzyConfigData

object MobzyConfig :
        PluginConfig<MobzyConfigData>(Holder),
        IMobzyConfigData by Holder.data {
    private object Holder : PluginConfigHolder<MobzyConfigData>(mobzy, MobzyConfigData.serializer())

    val creatureTypes: List<String> = listOf("MONSTER", "CREATURE", "AMBIENT", "WATER_CREATURE", "MISC")
    val registeredAddons: MutableList<MobzyAddon> = mutableListOf()
    val spawnCfgs: MutableList<SpawnConfig> = mutableListOf()
    val mobCfgs: MutableList<MobConfig> = mutableListOf()

    init {
        //first tick only finishes when all plugins are loaded, which is when we activate addons
        Bukkit.getServer().scheduler.runTaskLater(mobzy, Runnable { activateAddons() }, 1L)
    }

    override fun saveData() {
        super.saveData()
        spawnCfgs.forEach { it.saveData() }
    }

    /**
     * @param creatureType The name of the [EnumCreatureType].
     * @return The mob cap for that mob in config.
     */
    fun getMobCap(creatureType: String): Int = mobCaps[creatureType]
            ?: error("could not find mob cap for $creatureType")

    override fun reload(): ReloadContext.() -> Unit = {
        logSuccess("Reloading mobzy config")

        //We don't clear MobzyTypes since those will only ever change if an addon's code was changed which is impossible
        // to see during a soft reload like this.
        MobzyTemplates.clear()
        spawnCfgs.clear()
        mobCfgs.clear()
        unregisterSpawns()

        attempt("Loaded serialized config values", "Failed to load serialized config values") {
            loadData()
        }

        attempt("Reactivated all addonsMobzy", "Failed to reactive addons") {
            activateAddons()
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
        registeredAddons.forEach { spawnCfgs += loadSpawnCfg(it) }

        MobzyTemplates.loadTemplatesFromConfig()
        mobzy.registerSpawnTask()

        reloadExistingEntities()

        logSuccess("Registered addons: $registeredAddons")
    }

    /**
     * Loads a [SpawnConfig] for an addon
     *
     * @param plugin the addon registering it
     */
    fun loadSpawnCfg(plugin: MobzyAddon) {
        val spawnCfg = SpawnConfig(plugin.spawnConfig, plugin)
    }

    /**
     * Loads a [MobConfig] for an addon
     *
     * @param plugin the addon registering it
     */
    fun loadMobCfg(plugin: MobzyAddon) {
        val mobCfg = MobConfig(plugin.mobConfig, plugin)
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
}