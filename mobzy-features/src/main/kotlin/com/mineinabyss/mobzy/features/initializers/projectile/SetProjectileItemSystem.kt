package com.mineinabyss.mobzy.features.initializers.projectile

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.datatypes.ComponentDefinition
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.papermc.bridge.events.EventHelpers
import com.mineinabyss.geary.papermc.bridge.events.entities.OnSpawn
import com.mineinabyss.geary.systems.builders.listener
import com.mineinabyss.geary.systems.query.ListenerQuery
import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.idofront.typealiases.BukkitEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.ThrowableProjectile

@Serializable
@SerialName("mobzy:set.projectile_item")
data class SetProjectileItem(
    val item: SerializableItemStack
) {
    companion object : ComponentDefinition by EventHelpers.defaultTo<OnSpawn>()
}

@AutoScan
fun GearyModule.projectileItemSetter() = listener(object : ListenerQuery() {
    val bukkit by get<BukkitEntity>()
    val model by source.get<SetProjectileItem>()
}).exec {
    val projectile = bukkit as? ThrowableProjectile ?: return@exec
    projectile.item = model.item.toItemStack()
}
