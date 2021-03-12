package com.mineinabyss.mobzy

import com.mineinabyss.geary.ecs.api.engine.Engine
import com.mineinabyss.geary.ecs.api.engine.entity
import com.mineinabyss.geary.minecraft.components.SpawnBukkit
import com.mineinabyss.geary.minecraft.store.decodeComponentsFrom
import com.mineinabyss.idofront.config.IdofrontConfig
import com.mineinabyss.idofront.config.ReloadScope
import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.idofront.nms.aliases.NMSCreatureType
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.isCustomEntity
import com.mineinabyss.mobzy.configuration.SpawnConfig
import com.mineinabyss.mobzy.ecs.components.CopyNBT
import com.mineinabyss.mobzy.mobs.CustomEntity
import com.mineinabyss.mobzy.registration.MobzyNMSTypeInjector
import com.mineinabyss.mobzy.spawning.SpawnRegistry.unregisterSpawns
import com.mineinabyss.mobzy.spawning.SpawnTask
import kotlinx.serialization.Serializable
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

    override fun save() {
        super.save()
        spawnCfgs.forEach { it.save() }
    }

    /**
     * @param creatureType The name of the [EnumCreatureType].
     * @return The mob cap for that mob in config.
     */
    fun getCreatureTypeCap(creatureType: NMSCreatureType): Int = data.creatureTypeCaps[creatureType.toString()] ?: 0

    override fun ReloadScope.reload() {
        logSuccess("Reloading mobzy config")

        MobzyNMSTypeInjector.clear()
        //TODO PrefabManager.clearFromPlugin(mobzy)

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
    internal fun activateAddons() {
//        registeredAddons.forEach { spawnCfgs += it.loadSpawns() }

        MobzyNMSTypeInjector.injectDefaultAttributes()
        SpawnTask.startTask()

        fixEntitiesAfterReload()

        logSuccess("Registered addons: $registeredAddons")
        logSuccess("Loaded types: ${MobzyNMSTypeInjector.typeNames}")
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
                Engine.entity {
                    decodeComponentsFrom(oldEntity.persistentDataContainer)
                    set(CopyNBT(NBTTagCompound().apply { oldEntity.toNMS().load(this) }))
                    set(SpawnBukkit(oldEntity.location))
                }
                oldEntity.remove()
            }.count()
        }.sum()
        logSuccess("Reloaded $num custom entities")
    }
}
