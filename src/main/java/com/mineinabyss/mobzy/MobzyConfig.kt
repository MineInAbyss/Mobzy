package com.mineinabyss.mobzy

import com.mineinabyss.geary.minecraft.store.decode
import com.mineinabyss.idofront.config.IdofrontConfig
import com.mineinabyss.idofront.config.ReloadScope
import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.mobzy.api.nms.aliases.NMSCreatureType
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.nms.entity.typeName
import com.mineinabyss.mobzy.configuration.SpawnConfig
import com.mineinabyss.mobzy.mobs.CustomEntity
import com.mineinabyss.mobzy.mobs.MobType
import com.mineinabyss.mobzy.registration.MobzyTypeRegistry
import com.mineinabyss.mobzy.registration.MobzyTypes
import com.mineinabyss.mobzy.spawning.SpawnRegistry.unregisterSpawns
import com.mineinabyss.mobzy.spawning.SpawnTask
import com.okkero.skedule.schedule
import kotlinx.serialization.Serializable
import net.minecraft.server.v1_16_R2.EntityLiving
import net.minecraft.server.v1_16_R2.EnumCreatureType
import net.minecraft.server.v1_16_R2.NBTTagCompound
import org.bukkit.Bukkit

object MobzyConfig : IdofrontConfig<MobzyConfig.Data>(mobzy, Data.serializer()) {
    /**
     * @property debug whether the plugin is in a debug state (used primarily for broadcasting messages)
     * @property doMobSpawns whether custom mob spawning enabled
     * @property minChunkSpawnRad the minimum number of chunks away from the player in which a mob can spawn
     * @property maxChunkSpawnRad the maximum number of chunks away from the player in which a mob can spawn
     * @property maxCommandSpawns the maximum number of mobs to spawn with /mobzy spawn
     * @property playerGroupRadius the radius around which players will count mobs towards the local mob cap
     * @property spawnTaskDelay the delay in ticks between each attempted mob spawn
     * @property creatureTypeCaps Per-player mob caps for spawning of [NMSCreatureType]s on the server.
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
        var creatureTypeCaps: MutableMap<String, Int> = hashMapOf()
    )

    val creatureTypes: List<String> = listOf("MONSTER", "CREATURE", "AMBIENT", "WATER_CREATURE", "MISC")
    val registeredAddons: MutableList<MobzyAddon> = mutableListOf()
    val spawnCfgs: MutableList<SpawnConfig> = mutableListOf()

    init {
        //first tick only finishes when all plugins are loaded, which is when we activate addons
        mobzy.schedule {
            waitFor(1)
            activateAddons()
        }
    }

    override fun save() {
        super.save()
        spawnCfgs.forEach { it.save() }
    }

    /**
     * @param creatureType The name of the [EnumCreatureType].
     * @return The mob cap for that mob in config.
     */
    fun getCreatureTypeCap(creatureType: NMSCreatureType): Int = data.creatureTypeCaps[creatureType.toString()] ?: 0

    override fun reload(): ReloadScope.() -> Unit = {
        logSuccess("Reloading mobzy config")

        //We don't clear MobzyTypes since those will only ever change if an addon's code was changed which is impossible
        // to see during a soft reload like this.
        MobzyTypes.reset()
        spawnCfgs.clear()
        unregisterSpawns()

        //TODO make attempt show a bit of stacktrace
        attempt("Reactivating all addons") {
            activateAddons()
        }

        sender.success("Successfully reloaded config")
    }

    /**
     * Addons have registered themselves with the plugin at this point. We just need to parse their configs
     * and create everything they need for them.
     */
    private fun activateAddons() {
        MobzyTypeRegistry.clear()
        registeredAddons.forEach { it.loadMobTypes() }
//        registeredAddons.forEach { it.initializeMobs() }
        registeredAddons.forEach { spawnCfgs += it.loadSpawns() }

        MobzyTypeRegistry.injectDefaultAttributes()
        SpawnTask.startTask()

        fixEntitiesAfterReload()

        logSuccess("Registered addons: $registeredAddons")
        logSuccess("Loaded types: ${MobzyTypeRegistry.typeNames}")
    }

    /**
     * Loads a [SpawnConfig] for an addon
     *
     * @receiver The addon registering it
     */
    private fun MobzyAddon.loadSpawns() = SpawnConfig(spawnConfig, this)

    /**
     * Loads [MobType]s for an addon
     */
    private fun MobzyAddon.loadMobTypes() {
        MobzyTypes.registerTypes(this)
    }

    /**
     * Remove entities marked as a custom mob, but which are no longer considered an instance of CustomMob, and replace
     * them with the equivalent custom mob, transferring over the data.
     */
    private fun fixEntitiesAfterReload() {
        val num = Bukkit.getServer().worlds.map { world ->
            world.entities.filter {
                //is a custom mob but the nms entity is no longer an instance of CustomMob (likely due to a reload)
                it.scoreboardTags.contains(CustomEntity.ENTITY_VERSION) && it.toNMS() !is CustomEntity
            }.onEach { oldEntity ->
                //spawn a replacement entity of the same type as defined in scoreboard tags
                val replacement = oldEntity.persistentDataContainer.decode<MobType>()
                    ?.instantiateEntity(oldEntity.location)
                    ?: error("Failed to find a Mobzy type while reloading entity of type ${oldEntity.typeName}")

                val oldNMS = oldEntity.toNMS()
                val replacementNMS = replacement.nmsEntity

                if (oldNMS is EntityLiving && replacementNMS is EntityLiving) {
                    val nbt = NBTTagCompound()
                    //copies the entity nbt data to the compound
                    oldNMS.loadData(nbt)
                    //writes this nbt data to the replacement entity
                    replacementNMS.saveData(nbt)
                }

                oldEntity.remove()
            }.count()
        }.sum()
        logSuccess("Reloaded $num custom entities")
    }
}
