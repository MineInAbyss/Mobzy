package com.mineinabyss.mobzy.features.initializers.living

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.datatypes.ComponentDefinition
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.papermc.bridge.events.EventHelpers
import com.mineinabyss.geary.papermc.bridge.events.entities.OnSpawn
import com.mineinabyss.geary.systems.builders.listener
import com.mineinabyss.geary.systems.query.ListenerQuery
import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Mob

/**
 * A component for adding equipment to spawned mobs.
 */
@Serializable
@SerialName("mobzy:set.equipment")
data class SetEquipment(
    val helmet: SerializableItemStack? = SerializableItemStack(),
    val chestplate: SerializableItemStack? = SerializableItemStack(),
    val leggings: SerializableItemStack? = SerializableItemStack(),
    val boots: SerializableItemStack? = SerializableItemStack()
) {
    companion object : ComponentDefinition by EventHelpers.defaultTo<OnSpawn>()
}

@AutoScan
fun GearyModule.equipmentSetter() = listener(object : ListenerQuery() {
    val bukkit by get<BukkitEntity>()
    val equipment by source.get<SetEquipment>()
}).exec {
    val mob = bukkit as? Mob ?: return@exec
    mob.equipment.apply {
        equipment.helmet?.toItemStack()?.let { helmet = it }
        equipment.chestplate?.toItemStack()?.let { chestplate = it }
        equipment.leggings?.toItemStack()?.let { leggings = it }
        equipment.boots?.toItemStack()?.let { boots = it }
    }
}
