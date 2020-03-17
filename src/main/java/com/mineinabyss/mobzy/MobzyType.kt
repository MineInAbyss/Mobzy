package com.mineinabyss.mobzy

import com.charleskorn.kaml.Yaml
import com.mineinabyss.idofront.messaging.logInfo
import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.idofront.messaging.logWarn
import com.mineinabyss.mobzy.mobs.MobTemplate
import com.mineinabyss.mobzy.mobs.behaviours.AfterSpawnBehaviour
import com.mojang.datafixers.DataFixUtils
import com.mojang.datafixers.types.Type
import kotlinx.serialization.KSerializer
import kotlinx.serialization.internal.LinkedHashMapSerializer
import kotlinx.serialization.internal.StringSerializer
import net.minecraft.server.v1_15_R1.*
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld
import org.bukkit.event.entity.CreatureSpawnEvent
import kotlin.collections.Map
import kotlin.collections.MutableMap
import kotlin.collections.first
import kotlin.collections.mapOf
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.toMap

/**
 * @property types Used for getting a MobType from a String, which makes it easier to access from [MobTemplate]
 * @property templateNames Associates every mob id to a template name. We do this because some mobs may use the same
 * template, such as NPCs.
 * @property templates A map of mob [EntityTypes.mobName]s to [MobTemplate]s.
 */
class MobzyType {
    val types: Map<String, EntityTypes<*>>
        get() = _types.toMap()
    val templateNames: Map<String, String>
        get() = _templateNames.toMap()
    val templates: Map<String, MobTemplate>
        get() = _templates.toMap()

    private val _types: MutableMap<String, EntityTypes<*>> = mutableMapOf()
    private val _templateNames: MutableMap<String, String> = mutableMapOf()
    private var _templates: Map<String, MobTemplate> = mutableMapOf()

    fun registerEntity(name: String, type: EnumCreatureType, templateName: String, width: Float, height: Float, func: (World) -> Entity): EntityTypes<*> {
        val mobID = name.toEntityTypeID()
        val injected: EntityTypes<*> = injectNewEntity(mobID, "zombie", bToa(EntityTypes.b { _, world -> func(world) }, type).c().a(width, height))
        _types[mobID] = injected
        _templateNames[mobID] = templateName
        return injected
    }

    /**
     * Registers the mob attributes for each item in [types] by getting their associated template from config.
     * We end up with an updated [templates] list for reading from later.
     *
     * @see readTemplateConfig
     */
    fun registerTypes() {
        logInfo("Registering types")
        _templates = readTemplateConfig()
        logSuccess("Registered: ${types.keys}")
    }

    /**
     * Clears all stored [types], [templateNames], and [templates]
     */
    internal fun reload() { //TODO move all the CustomType related reload stuff here
        _types.clear()
        _templateNames.clear()
        _templates = mapOf()
    }

    private fun bToa(b: EntityTypes.b<Entity>, creatureType: EnumCreatureType): EntityTypes.a<Entity> = EntityTypes.a.a(b, creatureType)

    /**
     * Deserializes the template for a given mob from any of the registered mob configurations.
     *
     * @param name The name of the mob type to read the template for.
     */
    private fun readTemplateConfig(): Map<String, MobTemplate> =
            Yaml.default.parse((StringSerializer to MobTemplate.serializer()).map,
                    mobzy.mobzyConfig.mobCfgs.values.first { true }.saveToString()) //TODO run on all mobCfgs

    //TODO temporarily copied over while there are some problems with Shorthands.kt
    val <K, V> Pair<KSerializer<K>, KSerializer<V>>.map: KSerializer<Map<K, V>>
        get() = LinkedHashMapSerializer(this.first, this.second)

    /**
     * Injects an entity into the server
     *
     * Originally from https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293
     */
    private fun injectNewEntity(name: String, extend_from: String, a: EntityTypes.a<Entity>): EntityTypes<Entity> { //from https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293
        @Suppress("UNCHECKED_CAST") val dataTypes = DataConverterRegistry.a()
                .getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().worldVersion))
                .findChoiceType(DataConverterTypes.ENTITY).types() as MutableMap<String, Type<*>>
        if (dataTypes.containsKey("minecraft:$name")) logWarn("ALREADY CONTAINS KEY: $name")
        dataTypes["minecraft:$name"] = dataTypes["minecraft:$extend_from"]!!

        return IRegistry.a(IRegistry.ENTITY_TYPE, name, a.a(name))
    }

    /**
     * Spawns entity at specified Location
     *
     * @param entityTypes type of entity to spawn
     * @param loc         Location to spawn at
     * @return Reference to the spawned bukkit Entity
     */
    fun spawnEntity(entityTypes: EntityTypes<*>, loc: Location): org.bukkit.entity.Entity? {
        val nmsEntity = entityTypes.spawnCreature( // NMS method to spawn an entity from an EntityTypes
                (loc.world as CraftWorld?)!!.handle,  // reference to the NMS world
                null,  // EntityTag NBT compound
                null,  // custom name of entity
                null,  // player reference. used to know if player is OP to apply EntityTag NBT compound
                BlockPosition(loc.blockX, loc.blockY, loc.blockZ),  // the BlockPosition to spawn at
                EnumMobSpawn.NATURAL,
                false,
                false,
                CreatureSpawnEvent.SpawnReason.CUSTOM) // not sure. alters the Y position. this is only ever true when using spawn egg and clicked face is UP

        //Call a method after the entity has been spawned and things like location have been determined
        if (nmsEntity is AfterSpawnBehaviour) (nmsEntity as AfterSpawnBehaviour).afterSpawn()

        return nmsEntity?.bukkitEntity // convert to a Bukkit entity
    }
}