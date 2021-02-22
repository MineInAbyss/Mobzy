@file:JvmMultifileClass
@file:JvmName("MobzyAPI")

package com.mineinabyss.mobzy.api

import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.components.GearyPrefab
import com.mineinabyss.geary.ecs.components.get
import com.mineinabyss.geary.minecraft.store.*
import com.mineinabyss.mobzy.api.nms.aliases.BukkitEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityType
import com.mineinabyss.mobzy.api.nms.aliases.toBukkit
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.mobs.CustomEntity
import net.minecraft.server.v1_16_R2.BlockPosition
import net.minecraft.server.v1_16_R2.EnumMobSpawn
import net.minecraft.server.v1_16_R2.IChatBaseComponent
import net.minecraft.server.v1_16_R2.NBTTagCompound
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason

/**
 * Spawns entity at specified Location
 *
 * Originally from [paper forums](https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293)
 *
 * @param type The type of entity to spawn *
 * @return Reference to the spawned bukkit Entity
 */
fun Location.spawnEntity(
    type: NMSEntityType<*>,
    nbtTagCompound: NBTTagCompound? = null,
    customName: IChatBaseComponent? = null,
    playerReference: Player? = null,
    nmsSpawnType: EnumMobSpawn = EnumMobSpawn.NATURAL,
    ensureSpaceOrSomething: Boolean = true,
    spawnReason: SpawnReason = SpawnReason.DEFAULT
): BukkitEntity? {
    val nmsEntity = type.spawnCreature( // NMS method to spawn an entity from an EntityTypes
        world.toNMS(),  // reference to the NMS world
        nbtTagCompound,  // EntityTag NBT compound
        customName,  // custom name of entity
        playerReference?.toNMS(),  // player reference. used to know if player is OP to apply EntityTag NBT compound
        BlockPosition(this.blockX, this.blockY, this.blockZ),  // the BlockPosition to spawn at
        nmsSpawnType,
        ensureSpaceOrSomething, // does some sort of bounding box checks
        false,
        spawnReason
    ) // not sure. alters the Y position. this is only ever true when using spawn egg and clicked face is UP
    return nmsEntity?.toBukkit()
}


fun GearyEntity.instantiateMobzy(location: Location): CustomEntity? {
    val type = get<NMSEntityType<*>>() ?: return null
    val entity = location.spawnEntity(type) ?: return null
    val customEntity = entity.toMobzy() ?: error("Summoned mob was not a Mobzy entity")

    geary(entity) {
        val pdc = entity.persistentDataContainer
        //add persisting entity type component and encode it right away if not present
        if (!pdc.has<GearyPrefab>()) {
            pdc.encode(this@instantiateMobzy)
            decodeComponentsFrom(pdc)
        }
    }
    return customEntity
}
