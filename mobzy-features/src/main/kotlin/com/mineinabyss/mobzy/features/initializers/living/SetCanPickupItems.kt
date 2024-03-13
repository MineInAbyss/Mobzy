package com.mineinabyss.mobzy.features.initializers.living

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.datatypes.ComponentDefinition
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.papermc.bridge.events.EventHelpers
import com.mineinabyss.geary.papermc.bridge.events.entities.OnSpawn
import com.mineinabyss.geary.systems.builders.listener
import com.mineinabyss.geary.systems.query.ListenerQuery
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.LivingEntity

@Serializable
@SerialName("mobzy:set.can_pickup_items")
class SetCanPickupItems(val value: Boolean = true) {
    companion object : ComponentDefinition by EventHelpers.defaultTo<OnSpawn>()
}

@AutoScan
fun GearyModule.canPickUpItemsSetter() = listener(object : ListenerQuery() {
    val bukkit by get<BukkitEntity>()
    val pickup by source.get<SetCanPickupItems>()
}).exec {
    when (val mob = bukkit) {
        is LivingEntity -> mob.canPickupItems = pickup.value
    }
}
