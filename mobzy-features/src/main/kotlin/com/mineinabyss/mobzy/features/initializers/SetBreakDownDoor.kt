package com.mineinabyss.mobzy.features.initializers

import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Zombie

@JvmInline
@Serializable
@SerialName("mobzy:set.break_down_door")
value class SetBreakDownDoor(val value: Boolean = false)

@AutoScan
class SetBreakDownDoorListener : GearyListener() {
    private val TargetScope.breakDoor by onSet<SetBreakDownDoor>()
    private val TargetScope.bukkit by onSet<BukkitEntity>()

    @Handler
    fun TargetScope.apply() {
        when (val mob = bukkit) {
            is Zombie -> if (mob.supportsBreakingDoors()) mob.setCanBreakDoors(breakDoor.value)
            else error("Mob $mob does not support breaking doors but a component tried to set it.")
        }
    }
}
