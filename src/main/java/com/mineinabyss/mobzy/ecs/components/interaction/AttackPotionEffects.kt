package com.mineinabyss.mobzy.ecs.components.interaction

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.idofront.serialization.PotionSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.potion.PotionEffect

@Serializable
@SerialName("mobzy:potion_on_attack")
data class AttackPotionEffects(
        val effects: List<@Serializable(with = PotionSerializer::class) PotionEffect>,
        val applyChance: Double = 1.0,
) : GearyComponent
