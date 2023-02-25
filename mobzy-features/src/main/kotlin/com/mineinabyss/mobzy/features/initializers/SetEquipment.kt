package com.mineinabyss.mobzy.features.initializers

import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler

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
)

class SetEquipmentSystem : GearyListener() {
    private val TargetScope.equipment by onSet<SetEquipment>()
    private val TargetScope.bukkit by onSet<BukkitEntity>()

    @EventHandler
    fun TargetScope.apply() {
        val mob = bukkit as? Mob ?: error("Mob $bukkit is not a mob but a component tried to set its equipment.")
        mob.equipment.apply {
            equipment.helmet?.toItemStack()?.let { helmet = it }
            equipment.chestplate?.toItemStack()?.let { chestplate = it }
            equipment.leggings?.toItemStack()?.let { leggings = it }
            equipment.boots?.toItemStack()?.let { boots = it }
        }
    }
}
