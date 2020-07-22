package com.mineinabyss.mobzy

import com.mineinabyss.idofront.annotations.GenerateConfigExtensions
import com.mineinabyss.idofront.config.IdofrontConfig
import com.mineinabyss.idofront.config.ReloadScope
import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.mobzy.api.nms.aliases.NMSCreatureType
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.configuration.MobConfig
import com.mineinabyss.mobzy.configuration.SpawnConfig
import com.mineinabyss.mobzy.configuration.templates
import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.registration.MobzyTemplates
import com.mineinabyss.mobzy.registration.MobzyTypes
import com.mineinabyss.mobzy.registration.spawnEntity
import com.mineinabyss.mobzy.spawning.SpawnRegistry.unregisterSpawns
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_16_R1.EntityLiving
import net.minecraft.server.v1_16_R1.EnumCreatureType
import net.minecraft.server.v1_16_R1.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.entity.Entity

@GenerateConfigExtensions
object MobzyConfig : IdofrontConfig<MobzyConfig.Data>(mobzy, Data.serializer()) {
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
    @Serializable
    class Data(
            var debug: Boolean = false,
            var doMobSpawns: Boolean = false,
            var minChunkSpawnRad: Int = 3,
            var maxChunkSpawnRad: Int = 7,
            var maxCommandSpawns: Int = 50,
            var playerGroupRadius: Double = 128.0,
            var spawnTaskDelay: Long = 100,
            var mobCaps: MutableMap<String, Int> = hashMapOf()
    )

    val creatureTypes: List<String> = listOf("MONSTER", "CREATURE", "AMBIENT", "WATER_CREATURE", "MISC")
    val registeredAddons: MutableList<MobzyAddon> = mutableListOf()
    val spawnCfgs: MutableList<SpawnConfig> = mutableListOf()
    val mobTemplateCfgs: MutableList<MobConfig> = mutableListOf()

    init {
        //first tick only finishes when all plugins are loaded, which is when we activate addons
        Bukkit.getServer().scheduler.runTaskLater(mobzy, Runnable { activateAddons() }, 1L)
    }

    override fun save() {
        super.save()
        spawnCfgs.forEach { it.save() }
    }

    /**
     * @param creatureType The name of the [EnumCreatureType].
     * @return The mob cap for that mob in config.
     */
    fun getMobCap(creatureType: NMSCreatureType): Int = mobCaps[creatureType.toString()]
            ?: error("could not find mob cap for $creatureType")

    override fun reload(): ReloadScope.() -> Unit = {
        logSuccess("Reloading mobzy config")

        //We don't clear MobzyTypes since those will only ever change if an addon's code was changed which is impossible
        // to see during a soft reload like this.
        MobzyTemplates.reset()
        spawnCfgs.clear()
        mobTemplateCfgs.clear()
        unregisterSpawns()

        attempt("Reactivated all addonsMobzy", "Failed to reactive addons") {
            activateAddons()
        }

        sender.success("Successfully reloaded config")
    }

    /**
     * Addons have registered themselves with the plugin at this point. We just need to parse their configs
     * and create everything they need for them.
     */
    private fun activateAddons() {
        registeredAddons.forEach { loadMobCfg(it) }
        registeredAddons.forEach { it.initializeMobs() }
        registeredAddons.forEach { spawnCfgs += loadSpawnCfg(it) }

        MobzyTypes.injectDefaultAttributes()
        mobzy.registerSpawnTask()

        reloadExistingEntities()

        logSuccess("Registered addons: $registeredAddons")
        logSuccess("Loaded types: ${MobzyTypes.typeNames}")
    }

    /**
     * Loads a [SpawnConfig] for an addon
     *
     * @param plugin the addon registering it
     */
    fun loadSpawnCfg(plugin: MobzyAddon) = SpawnConfig(plugin.spawnConfig, plugin)

    /**
     * Loads a [MobConfig] for an addon
     *
     * @param plugin the addon registering it
     */
    fun loadMobCfg(plugin: MobzyAddon) {
        val mobCfg = MobConfig(plugin.mobConfig, plugin)
        MobzyTemplates.registerTemplates(mobCfg.templates)
        mobTemplateCfgs += mobCfg
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
                val replacement = it.location.spawnEntity(it.toNMSEntityTypeViaScoreboardTags())?.toNMS<EntityLiving>()
                val nbt = NBTTagCompound()
                it.toNMS<EntityLiving>().loadData(nbt) //.b copies over the entity's nbt data to the compound
                it.remove()
                replacement?.save(nbt) //.a copies the nbt data to the new entity
                num++
            }
        }
        logSuccess("Reloaded $num custom entities")
    }

    private fun Entity.toNMSEntityTypeViaScoreboardTags(): NMSEntityType<*> = MobzyTypes[scoreboardTags.first { MobzyTypes.contains(it) }]
}