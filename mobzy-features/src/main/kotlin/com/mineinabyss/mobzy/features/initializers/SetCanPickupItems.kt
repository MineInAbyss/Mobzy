package com.mineinabyss.mobzy.features.initializers

import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.LivingEntity

@Serializable
@SerialName("mobzy:set.can_pickup_items")
class SetCanPickupItems(val value: Boolean)

@AutoScan
class SetCanPickupItemsSystem : GearyListener() {
    private val TargetScope.pickup by onSet<SetCanPickupItems>()
    private val TargetScope.bukkit by onSet<BukkitEntity>()

    @Handler
    fun TargetScope.apply() {
        when (val mob = bukkit) {
            is LivingEntity -> mob.canPickupItems = pickup.value
            else -> error("Cannot set canPickupItems on $mob")
        }
    }
}
