package com.mineinabyss.mobzy.ecs.behaviors

import com.mineinabyss.mobzy.ecs.components.MobzyComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
@SerialName("mobzy:behavior.tempt")
class TemptBehavior(
        val items: List<Material>,
        val speed: Double = 1.0,
        val range: Double = 8.0
): MobzyComponent