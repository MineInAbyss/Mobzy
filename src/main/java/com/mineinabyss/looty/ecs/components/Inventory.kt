package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.idofront.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import java.util.*

@Serializable
class Inventory(
        @Serializable(with = UUIDSerializer::class)
        val uuid: UUID
) : MobzyComponent() {
    private val player get() = Bukkit.getPlayer(uuid) ?: error("UUID is not a player")

    val inventory get() = player.inventory

    @Transient
    var itemCache = mapOf<Int, Pair<ItemStack, Int>>()
        internal set

    operator fun component1() = inventory
    operator fun component2() = itemCache
}