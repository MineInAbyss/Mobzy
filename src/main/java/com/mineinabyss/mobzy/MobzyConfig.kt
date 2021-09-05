package com.mineinabyss.mobzy

import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.geary.minecraft.access.toGeary
import com.mineinabyss.geary.minecraft.spawnGeary
import com.mineinabyss.geary.minecraft.store.decodeComponentsFrom
import com.mineinabyss.idofront.config.IdofrontConfig
import com.mineinabyss.idofront.config.ReloadScope
import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.idofront.nms.aliases.NMSCreatureType
import com.mineinabyss.idofront.nms.aliases.NMSDataContainer
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.serialization.IntRangeSerializer
import com.mineinabyss.idofront.time.TimeSpan
import com.mineinabyss.mobzy.api.isCustomEntity
import com.mineinabyss.mobzy.configuration.SpawnConfig
import com.mineinabyss.mobzy.ecs.components.CopyNBT
import com.mineinabyss.mobzy.mobs.CustomEntity
import com.mineinabyss.mobzy.registration.MobzyNMSTypeInjector
import com.mineinabyss.mobzy.spawning.MobCategory
import com.mineinabyss.mobzy.spawning.SpawnRegistry.unregisterSpawns
import com.mineinabyss.mobzy.spawning.SpawnTask
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import java.util.*

val mobzyConfig get() = MobzyConfig.data

object MobzyConfig : IdofrontConfig<MobzyConfig.Data>(mobzy, Data.serializer()) {
    /**
     * @property debug whether the plugin is in a debug state (used primarily for broadcasting messages)
     * @property doMobSpawns whether custom mob spawning enabled
     * @property chunkSpawnRad the minimum number of chunks away from the player in which a mob can spawn
     * @property maxChunkSpawnRad the maximum number of chunks away from the player in which a mob can spawn
     * @property maxCommandSpawns the maximum number of mobs to spawn with /mobzy spawn
     * @property playerGroupRadius the radius around which players will count mobs towards the local mob cap
     * @property spawnTaskDelay the delay in ticks between each attempted mob spawn
     * @property creatureTypeCaps Per-player mob caps for spawning of [NMSCreatureType]s on the server.
     * @property spawnHeightRange The maximum amount above or below players that mobs can spawn.
     */
    @Serializable
    class Data(
        var debug: Boolean = false,
        var doMobSpawns: Boolean = false,
        @Serializable(with = IntRangeSerializer::class)
        var chunkSpawnRad: IntRange,
        var maxCommandSpawns: Int,
        var playerGroupRadius: Double,
        var spawnTaskDelay: TimeSpan,
        var creatureTypeCaps: MutableMap<MobCategory, Int> = mutableMapOf(),
        var spawnHeightRange: Int,
    )

    /**
     * @param creatureType The name of the [EnumCreatureType].
     * @return The mob cap for that mob in config.
     */
    fun getCreatureTypeCap(creatureType: MobCategory): Int = data.creatureTypeCaps[creatureType] ?: 0

    internal fun reloadSpawns() {
        unregisterSpawns()
    }

    override fun ReloadScope.unload() {
        //TODO PrefabManager.clearFromPlugin(mobzy)

        "Clear registered types" {
            MobzyNMSTypeInjector.clear()
        }

        "Stop spawn task" {
            SpawnTask.stopTask()
        }
    }

    override fun ReloadScope.load() {
        logSuccess("Loading Mobzy config")

        "Inject mob attributes" {
            MobzyNMSTypeInjector.injectDefaultAttributes()
        }

        "Load spawns" {
            reloadSpawns()
        }
        "Start spawn task" {
            SpawnTask.startTask()
        }

        "Fix old entities after reload" {
            fixEntitiesAfterReload()
        }

        sender.success("Loaded types: ${MobzyNMSTypeInjector.typeNames}")
        sender.success("Successfully loaded config")
    }

    /**
     * Loads a [SpawnConfig] for an addon
     *
     * @receiver The addon registering it
     */
    private fun MobzyAddon.loadSpawns() = SpawnConfig(spawnConfig, this)

    /**
     * Remove entities marked as a custom mob, but which are no longer considered an instance of CustomMob, and replace
     * them with the equivalent custom mob, transferring over the data.
     */
    private fun fixEntitiesAfterReload() {
        val num = Bukkit.getServer().worlds.map { world ->
            world.entities.filter {
                //is a custom mob but the nms entity is no longer an instance of CustomMob (likely due to a reload)
                it.scoreboardTags.contains(CustomEntity.ENTITY_VERSION) && !it.isCustomEntity
            }.onEach { oldEntity ->
                //spawn a replacement entity and copy this entity's NBT over to it
                val prefab = oldEntity.toGeary().get<PrefabKey>() ?: return@onEach //TODO handle better or error
                (oldEntity.location.spawnGeary(prefab) ?: return@onEach).toGeary {
                    decodeComponentsFrom(oldEntity.persistentDataContainer)
                    set(CopyNBT(NMSDataContainer().apply { oldEntity.toNMS().save(this) }))
                }
                oldEntity.remove()
            }.count()
        }.sum()
        logSuccess("Reloaded $num custom entities")
    }
}
