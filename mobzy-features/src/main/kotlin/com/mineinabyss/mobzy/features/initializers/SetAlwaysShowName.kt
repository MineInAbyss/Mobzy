package com.mineinabyss.mobzy.features.initializers

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

@JvmInline
@Serializable
@SerialName("mobzy:set.always_show_name")
value class SetAlwaysShowName(val value: Boolean = true) {
    companion object : ComponentDefinition by EventHelpers.defaultTo<OnSpawn>()
}

@AutoScan
fun GearyModule.customNameVisibleSetter() = listener(object : ListenerQuery() {
    val bukkit by get<BukkitEntity>()
    val visibility by source.get<SetAlwaysShowName>()
}).exec {
    bukkit.isCustomNameVisible = visibility.value
}
