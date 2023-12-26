package com.mineinabyss.mobzy.features.initializers.zombie

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
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
    private val Pointers.breakDoor by get<SetBreakDownDoor>().whenSetOnTarget()
    private val Pointers.bukkit by get<BukkitEntity>().whenSetOnTarget()

    override fun Pointers.handle() {
        when (val mob = bukkit) {
            is Zombie -> if (mob.supportsBreakingDoors()) mob.setCanBreakDoors(breakDoor.value)
        }
    }
}
