package com.mineinabyss.mobzy.features.initializers.zombie

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
import org.bukkit.entity.Zombie

@JvmInline
@Serializable
@SerialName("mobzy:set.break_down_door")
value class SetBreakDownDoor(val value: Boolean = false) {
    companion object : ComponentDefinition by EventHelpers.defaultTo<OnSpawn>()
}

@AutoScan
fun GearyModule.breakDownDoorSetter() = listener(object : ListenerQuery() {
    val bukkit by get<BukkitEntity>()
    val breakDoor by source.get<SetBreakDownDoor>()
}).exec {
    when (val mob = bukkit) {
        is Zombie -> if (mob.supportsBreakingDoors()) mob.setCanBreakDoors(breakDoor.value)
    }
}
