package com.mineinabyss.mobzy.features.initializers

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.AbstractSkeleton
import org.bukkit.entity.Phantom
import org.bukkit.entity.Zombie

@JvmInline
@Serializable
@SerialName("mobzy:set.burn_in_day")
value class SetBurnInDay(val value: Boolean = true)

@AutoScan
class SetBurnInDaySystem : GearyListener() {
    private val Pointers.burn by get<SetBurnInDay>().whenSetOnTarget()
    private val Pointers.bukkit by get<BukkitEntity>().whenSetOnTarget()

    override fun Pointers.handle() {
        when (val mob = bukkit) {
            is Phantom -> mob.setShouldBurnInDay(burn.value)
            is AbstractSkeleton -> mob.setShouldBurnInDay(burn.value)
            is Zombie -> mob.setShouldBurnInDay(burn.value)
        }
    }
}
