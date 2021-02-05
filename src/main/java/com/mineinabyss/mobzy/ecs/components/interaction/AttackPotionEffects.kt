package com.mineinabyss.mobzy.ecs.components.interaction

import com.mineinabyss.geary.ecs.autoscan.AutoscanComponent
import com.mineinabyss.idofront.serialization.PotionEffectSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.potion.PotionEffect

/**
 * A component for applying potion effects when attacking.
 *
 * @param effects A list of effects to apply.
 * @param applyChance A chance from 0 to 1 to apply those effects.
 */
@Serializable
@SerialName("mobzy:potion_on_attack")
@AutoscanComponent
data class AttackPotionEffects(
    val effects: List<@Serializable(with = PotionEffectSerializer::class) PotionEffect>,
    val applyChance: Double = 1.0,
)
