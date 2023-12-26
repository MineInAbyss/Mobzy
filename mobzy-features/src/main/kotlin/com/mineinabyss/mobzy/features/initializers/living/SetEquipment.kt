package com.mineinabyss.mobzy.features.initializers.living

import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
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
)

class SetEquipmentSystem : GearyListener() {
    private val Pointers.equipment by get<SetEquipment>().whenSetOnTarget()
    private val Pointers.bukkit by get<BukkitEntity>().whenSetOnTarget()

    override fun Pointers.handle() {
        val mob = bukkit as? Mob ?: return
        mob.equipment.apply {
            equipment.helmet?.toItemStack()?.let { helmet = it }
            equipment.chestplate?.toItemStack()?.let { chestplate = it }
            equipment.leggings?.toItemStack()?.let { leggings = it }
            equipment.boots?.toItemStack()?.let { boots = it }
        }
    }
}
