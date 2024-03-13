package com.mineinabyss.mobzy.features.initializers.sheep

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
import org.bukkit.DyeColor
import org.bukkit.material.Colorable

@JvmInline
@Serializable
@SerialName("mobzy:set.wool_color")
value class SetWoolColor(val value: DyeColor) {
    companion object : ComponentDefinition by EventHelpers.defaultTo<OnSpawn>()
}

@AutoScan
fun GearyModule.setWoolColorAction() = listener(object : ListenerQuery() {
    val bukkit by get<BukkitEntity>()
    val woolColor by source.get<SetWoolColor>()
}).exec {
    entity.set(SetWoolColor(woolColor.value))
    when (val mob = bukkit) {
        is Colorable -> mob.color = woolColor.value
    }
}
