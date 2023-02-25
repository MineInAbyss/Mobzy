package com.mineinabyss.mobzy.features.initializers

import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.AbstractSkeleton
import org.bukkit.entity.Phantom
import org.bukkit.entity.Zombie

@Serializable
@SerialName("mobzy:set.burn_in_day")
class SetBurnInDay(val value: Boolean = true)

@AutoScan
class SetBurnInDaySystem : GearyListener() {
    private val TargetScope.burn by onSet<SetBurnInDay>()
    private val TargetScope.bukkit by onSet<BukkitEntity>()

    @Handler
    fun TargetScope.apply() {
        when (val mob = bukkit) {
            is Phantom -> mob.setShouldBurnInDay(burn.value)
            is AbstractSkeleton -> mob.setShouldBurnInDay(burn.value)
            is Zombie -> mob.setShouldBurnInDay(burn.value)
            else -> error("Tried setting can burn in daylight for a mob that doesn't have that property: $mob")
        }
    }
}
