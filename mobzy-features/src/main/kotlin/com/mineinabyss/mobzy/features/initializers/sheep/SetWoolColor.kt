package com.mineinabyss.mobzy.features.initializers.sheep

import com.mineinabyss.geary.annotations.optin.UnsafeAccessors
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.datatypes.ComponentDefinition
import com.mineinabyss.geary.papermc.bridge.events.EventHelpers
import com.mineinabyss.geary.papermc.bridge.events.entities.OnSpawn
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
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
class SetWoolColorSystem : GearyListener() {
    private val Pointers.bukkit by get<BukkitEntity>().on(target)
    private val Pointers.woolColor by get<SetWoolColor>().on(source)

    @OptIn(UnsafeAccessors::class)
    override fun Pointers.handle() {
        target.entity.set(SetWoolColor(woolColor.value))
        when (val mob = bukkit) {
            is Colorable -> mob.color = woolColor.value
        }
    }
}
