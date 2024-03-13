package com.mineinabyss.mobzy.features.initializers.slime

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.datatypes.ComponentDefinition
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.papermc.bridge.events.EventHelpers
import com.mineinabyss.geary.papermc.bridge.events.entities.OnSpawn
import com.mineinabyss.geary.systems.builders.listener
import com.mineinabyss.geary.systems.query.ListenerQuery
import com.mineinabyss.idofront.serialization.IntRangeSerializer
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.idofront.util.randomOrMin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Slime

@JvmInline
@Serializable
@SerialName("mobzy:set.slime_size")
value class SetSlimeSize(@Serializable(with = IntRangeSerializer::class) val size: IntRange) {
    companion object : ComponentDefinition by EventHelpers.defaultTo<OnSpawn>()
}

@AutoScan
fun GearyModule.slimeSizeSetter() = listener(object : ListenerQuery() {
    val bukkit by get<BukkitEntity>()
    val slimeSize by source.get<SetSlimeSize>()
}).exec {
    val slime = (bukkit as? Slime) ?: return@exec
    slime.size = slimeSize.size.randomOrMin()
}
