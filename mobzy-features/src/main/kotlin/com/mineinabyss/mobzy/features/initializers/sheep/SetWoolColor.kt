package com.mineinabyss.mobzy.features.initializers.sheep

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.DyeColor
import org.bukkit.entity.Sheep

@JvmInline
@Serializable
@SerialName("mobzy:set.wool_color")
value class SetWoolColor(val value: DyeColor)

@AutoScan
class SetWoolColorSystem : GearyListener() {
    private val Pointers.woolColor by get<SetWoolColor>().whenSetOnTarget()
    private val Pointers.bukkit by get<BukkitEntity>().whenSetOnTarget()

    override fun Pointers.handle() {
        when(val mob = bukkit) {
            is Sheep -> mob.color = woolColor.value
        }
    }
}
