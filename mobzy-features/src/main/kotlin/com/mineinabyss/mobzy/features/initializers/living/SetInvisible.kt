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
@SerialName("mobzy:set.invisible")
value class SetInvisible(val value: Boolean = true)

@AutoScan
class SetInvisibleSystem : GearyListener() {
    private val Pointers.visibility by get<SetInvisible>().whenSetOnTarget()
    private val Pointers.bukkit by get<BukkitEntity>().whenSetOnTarget()

    override fun Pointers.handle() {
        val entity = (bukkit as? LivingEntity) ?: return
        entity.isInvisible = visibility.value
    }
}
