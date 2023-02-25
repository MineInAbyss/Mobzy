package com.mineinabyss.mobzy.features.initializers

import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
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
    private val TargetScope.model by onSet<SetProjectileItem>()
    private val TargetScope.bukkit by onSet<BukkitEntity>()

    @Handler
    fun TargetScope.applyModel() {
        val projectile = bukkit as? ThrowableProjectile
            ?: error("Tried to apply projectile model to a non-projectile entity: $bukkit")
        projectile.item = model.item.toItemStack()
    }
}
