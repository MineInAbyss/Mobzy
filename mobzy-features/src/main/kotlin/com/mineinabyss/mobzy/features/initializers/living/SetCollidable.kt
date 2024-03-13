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

@JvmInline
@Serializable
@SerialName("mobzy:set.collidable")
value class SetCollidable(val value: Boolean = true) {
    companion object : ComponentDefinition by EventHelpers.defaultTo<OnSpawn>()
}

@AutoScan
fun GearyModule.collidableSetter() = listener(object : ListenerQuery() {
    val bukkit by get<BukkitEntity>()
    val collidable by source.get<SetCollidable>()
}).exec {
    val entity = (bukkit as? LivingEntity) ?: return@exec
    entity.isCollidable = collidable.value
}
