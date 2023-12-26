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
@SerialName("mobzy:set.always_show_name")
value class SetAlwaysShowName(val value: Boolean = true)

@AutoScan
class SetAlwaysShowNameSystem : GearyListener() {
    private val Pointers.visibility by get<SetAlwaysShowName>().whenSetOnTarget()
    private val Pointers.bukkit by get<BukkitEntity>().whenSetOnTarget()

    override fun Pointers.handle() {
        bukkit.isCustomNameVisible = visibility.value
    }
}
