package com.mineinabyss.mobzy.features.initializers.living

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.LivingEntity

@JvmInline
@Serializable
@SerialName("mobzy:set.collidable")
value class SetCollidable(val value: Boolean = true)

@AutoScan
class SetCollidableSystem : GearyListener() {
    private val Pointers.collidable by get<SetCollidable>().whenSetOnTarget()
    private val Pointers.bukkit by get<BukkitEntity>().whenSetOnTarget()

    override fun Pointers.handle() {
        val entity = (bukkit as? LivingEntity) ?: return
        entity.isCollidable = collidable.value
    }
}
