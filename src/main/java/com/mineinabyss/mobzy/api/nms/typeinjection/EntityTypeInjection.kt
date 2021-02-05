package com.mineinabyss.mobzy.api.nms.typeinjection

import com.mineinabyss.idofront.events.call
import com.mineinabyss.idofront.messaging.logWarn
import com.mineinabyss.mobzy.api.nms.aliases.BukkitEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.ecs.events.MobzySpawnEvent
import com.mineinabyss.mobzy.mobs.CustomEntity
import com.mojang.datafixers.DataFixUtils
import com.mojang.datafixers.types.Type
import net.minecraft.server.v1_16_R2.*
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld
import org.bukkit.event.entity.CreatureSpawnEvent

typealias NMSRegistry<T> = IRegistry<T>

/**
 * Registers an [NMSEntityType] with the server.
 */
fun <T : NMSEntity> NMSEntityType<T>.registerEntityType(key: String): NMSEntityType<T> =
    NMSRegistry.a(NMSRegistry.ENTITY_TYPE, key, this)

/**
 * Injects an entity into the server
 *
 * Originally from [paper forums](https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293)
 */
fun NMSEntityTypeBuilder.injectType(
    key: String,
    extendFrom: String
): NMSEntityType<Entity> { //from https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293
    @Suppress("UNCHECKED_CAST") val dataTypes = NMSDataConverterRegistry.getDataFixer()
        .getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().worldVersion))
        .findChoiceType(DataConverterTypes.ENTITY).types() as MutableMap<String, Type<*>>
    if (dataTypes.containsKey("minecraft:$key")) logWarn("ALREADY CONTAINS KEY: $key")
    dataTypes["minecraft:$key"] = dataTypes["minecraft:$extendFrom"]!!

    return build(key).registerEntityType(key)
}

/**
 * Spawns entity at specified Location
 *
 * Originally from [paper forums](https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293)
 *
 * @param type The type of entity to spawn *
 * @return Reference to the spawned bukkit Entity
 */
fun Location.spawnEntity(type: NMSEntityType<*>): BukkitEntity? {
    val nmsEntity = type.spawnCreature( // NMS method to spawn an entity from an EntityTypes
        (this.world as CraftWorld?)!!.handle,  // reference to the NMS world
        null,  // EntityTag NBT compound
        null,  // custom name of entity
        null,  // player reference. used to know if player is OP to apply EntityTag NBT compound
        BlockPosition(this.blockX, this.blockY, this.blockZ),  // the BlockPosition to spawn at
        EnumMobSpawn.NATURAL,
        false,
        false,
        CreatureSpawnEvent.SpawnReason.CUSTOM
    ) // not sure. alters the Y position. this is only ever true when using spawn egg and clicked face is UP

    (nmsEntity as? CustomEntity)?.run { MobzySpawnEvent(this).call() }

    return nmsEntity?.bukkitEntity // convert to a Bukkit entity
}
