package com.mineinabyss.mobzy.features.initializers.monster

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
import org.bukkit.entity.AbstractSkeleton
import org.bukkit.entity.Phantom
import org.bukkit.entity.Zombie

@JvmInline
@Serializable
@SerialName("mobzy:set.burn_in_day")
value class SetBurnInDay(val value: Boolean = true) {
    companion object : ComponentDefinition by EventHelpers.defaultTo<OnSpawn>()
}

@AutoScan
fun GearyModule.burnInDaySetter() = listener(object : ListenerQuery() {
    val bukkit by get<BukkitEntity>()
    val burn by source.get<SetBurnInDay>()
}).exec {
    when (val mob = bukkit) {
        is Phantom -> mob.setShouldBurnInDay(burn.value)
        is AbstractSkeleton -> mob.setShouldBurnInDay(burn.value)
        is Zombie -> mob.setShouldBurnInDay(burn.value)
    }
}
