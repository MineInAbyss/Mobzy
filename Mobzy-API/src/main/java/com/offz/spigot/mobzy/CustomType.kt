package com.offz.spigot.mobzy

import com.mineinabyss.idofront.messaging.logInfo
import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.idofront.messaging.logWarn
import com.mojang.datafixers.DataFixUtils
import com.mojang.datafixers.types.Type
import com.offz.spigot.mobzy.mobs.MobTemplate
import com.offz.spigot.mobzy.mobs.MobTemplate.Companion.deserialize
import com.offz.spigot.mobzy.mobs.behaviours.AfterSpawnBehaviour
import net.minecraft.server.v1_15_R1.*
import org.bukkit.Location
import org.bukkit.configuration.MemorySection
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld
import org.bukkit.event.entity.CreatureSpawnEvent

/**
 * @property types Used for getting a MobType from a String, which makes it easier to access from [MobTemplate]
 */
class CustomType {
    val types: Map<String, EntityTypes<*>>
        get() = _types.toMap()
    val templateNames: Map<String, String>
        get() = _templateNames.toMap()
    val templates: Map<String, MobTemplate>
        get() = _templates.toMap()

    private val _types: MutableMap<String, EntityTypes<*>> = mutableMapOf()
    private val _templateNames: MutableMap<String, String> = mutableMapOf()
    private val _templates: MutableMap<String, MobTemplate> = mutableMapOf()

    fun registerEntity(name: String, type: EnumCreatureType, templateName: String, width: Float, height: Float, func: (World) -> Entity): EntityTypes<*> {
        val mobID = name.toEntityTypeID()
        val injected: EntityTypes<*> = injectNewEntity(mobID, "zombie", bToa(EntityTypes.b { _, world -> func(world) }, type).c().a(width, height))
        _types[mobID] = injected
        _templateNames[mobID] = templateName
        return injected
    }

    fun registerTypes() {
        logInfo("Registering types")
        for (type in _types.values) {
            val name = type.mobName
            _templates[name] = readTemplateConfig(_templateNames[name]!!)
        }
        logSuccess("Registered: ${types.keys}")
    }

    internal fun reload() { //TODO move all the CustomType related reload stuff here
        _types.clear()
        _templateNames.clear()
        _templates.clear()
    }

    private fun bToa(b: EntityTypes.b<Entity>, creatureType: EnumCreatureType): EntityTypes.a<Entity> = EntityTypes.a.a(b, creatureType)

    private fun readTemplateConfig(name: String): MobTemplate =
            deserialize(((mobzy.mobzyConfig.mobCfgs.values.firstOrNull { it.contains(name) }
                    ?: error("$name's builder not found"))
                    [name] as MemorySection)
                    .getValues(true), name)

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

        //testing the enoughSpace method
//        if (!MobSpawn.enoughSpace(loc, nmsEntity.width, nmsEntity.length))
//            nmsEntity.die();
        return nmsEntity?.bukkitEntity // convert to a Bukkit entity
    }
}