package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.idofront.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import java.util.*

@Serializable
class PlayerComponent(
        @Serializable(with = UUIDSerializer::class)
        val uuid: UUID
) : MobzyComponent() {
    val player get() = Bukkit.getPlayer(uuid) ?: error("UUID is not a player")

    operator fun component1() = player
}