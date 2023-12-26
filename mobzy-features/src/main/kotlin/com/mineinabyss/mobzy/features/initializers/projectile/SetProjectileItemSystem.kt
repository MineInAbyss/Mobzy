package com.mineinabyss.mobzy.features.initializers.projectile

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.ThrowableProjectile

@Serializable
@SerialName("mobzy:set.projectile_item")
data class SetProjectileItem(
    val item: SerializableItemStack
)

@AutoScan
class SetProjectileItemSystem : GearyListener() {
    private val Pointers.model by get<SetProjectileItem>().whenSetOnTarget()
    private val Pointers.bukkit by get<BukkitEntity>().whenSetOnTarget()

    override fun Pointers.handle() {
        val projectile = bukkit as? ThrowableProjectile ?: return
        projectile.item = model.item.toItemStack()
    }
}
