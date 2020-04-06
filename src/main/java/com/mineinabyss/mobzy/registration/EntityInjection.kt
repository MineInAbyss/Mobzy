package com.mineinabyss.mobzy.registration

import com.mineinabyss.idofront.messaging.logWarn
import com.mineinabyss.mobzy.mobs.behaviours.AfterSpawnBehaviour
import com.mojang.datafixers.DataFixUtils
import com.mojang.datafixers.types.Type
import net.minecraft.server.v1_15_R1.*
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld
import org.bukkit.event.entity.CreatureSpawnEvent

internal fun bToa(b: EntityTypes.b<Entity>, creatureType: EnumCreatureType): EntityTypes.a<Entity> = EntityTypes.a.a(b, creatureType)

/**
 * Injects an entity into the server
 *
 * Originally from [paper forms](https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293)
 */
internal fun injectNewEntity(name: String, extend_from: String, a: EntityTypes.a<Entity>): EntityTypes<Entity> { //from https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293
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
internal fun spawnEntity(entityTypes: EntityTypes<*>, loc: Location): org.bukkit.entity.Entity? {
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