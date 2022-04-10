package com.mineinabyss.mobzy

import com.mineinabyss.geary.ecs.api.entities.with
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.geary.papermc.spawnFromPrefab
import com.mineinabyss.geary.papermc.store.decodeComponentsFrom
import com.mineinabyss.geary.papermc.store.decodePrefabs
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.config.IdofrontConfig
import com.mineinabyss.idofront.config.ReloadScope
import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.CopyNBT
import com.mineinabyss.mobzy.ecs.components.MobCategory
import com.mineinabyss.mobzy.ecs.components.initialization.MobzyType
import com.mineinabyss.mobzy.injection.MobzyNMSTypeInjector
import com.mineinabyss.mobzy.injection.extendsCustomClass
import com.mineinabyss.mobzy.spawning.SpawnRegistry
import com.mineinabyss.mobzy.spawning.SpawnTask
import net.minecraft.nbt.CompoundTag
import org.bukkit.Bukkit
import java.util.*

class MobzyConfigImpl(
    val nmsTypeInjector: MobzyNMSTypeInjector
) : IdofrontConfig<MobzyConfig.Data>(mobzy, MobzyConfig.Data.serializer()), MobzyConfig {
    /**
     * @param creatureType The name of the [EnumCreatureType].
     * @return The mob cap for that mob in config.
     */
    override fun getCreatureTypeCap(creatureType: MobCategory): Int = data.creatureTypeCaps[creatureType] ?: 0

    override fun ReloadScope.unload() {
        //TODO PrefabManager.clearFromPlugin(mobzy)

        "Clear registered types" {
            nmsTypeInjector.clear()
        }

        "Stop spawn task" {
            SpawnTask.stopTask()
        }
    }

    override fun ReloadScope.load() {
        logSuccess("Loading Mobzy config")

        "Inject mob attributes" {
            nmsTypeInjector.injectDefaultAttributes()
        }

        "Spawns" {
            !"Load spawns" {
                SpawnRegistry.reloadSpawns()
            }
            !"Start spawn task" {
                SpawnTask.startTask()
            }
        }

        "Fix old entities after reload" {
            fixEntitiesAfterReload()
        }

        sender.success("Loaded types: ${nmsTypeInjector.typeNames}")
        sender.success("Successfully loaded config")
    }

    /**
     * Remove entities marked as a custom mob, but which are no longer considered an instance of CustomMob, and replace
     * them with the equivalent custom mob, transferring over the data.
     */
    private fun fixEntitiesAfterReload() {
        val num = Bukkit.getServer().worlds.map { world ->
            world.entities.filter {
                //is a custom mob but the nms entity is no longer an instance of CustomMob (likely due to a reload)
                it.persistentDataContainer.decodePrefabs().any { prefab ->
                    prefab.toEntity()?.has<MobzyType>() == true
                } && !it.extendsCustomClass
            }.onEach { oldEntity ->
                //spawn a replacement entity and copy this entity's NBT over to it
                oldEntity.toGeary().with { prefab: PrefabKey ->
                    (oldEntity.location.spawnFromPrefab(prefab) ?: return@onEach).toGeary {
                        decodeComponentsFrom(oldEntity.persistentDataContainer)
                        set(CopyNBT(CompoundTag().apply { oldEntity.toNMS().save(this) }))
                    }
                    oldEntity.remove()
                }
            }.count()
        }.sum()
        logSuccess("Reloaded $num custom entities")
    }
}
