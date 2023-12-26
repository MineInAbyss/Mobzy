package com.mineinabyss.mobzy.features.initializers.living

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.LivingEntity

@Serializable
@SerialName("mobzy:set.can_pickup_items")
class SetCanPickupItems(val value: Boolean = true)

@AutoScan
class SetCanPickupItemsSystem : GearyListener() {
    private val Pointers.pickup by get<SetCanPickupItems>().whenSetOnTarget()
    private val Pointers.bukkit by get<BukkitEntity>().whenSetOnTarget()
    override fun Pointers.handle() {
        when (val mob = bukkit) {
            is LivingEntity -> mob.canPickupItems = pickup.value
        }
    }
}
