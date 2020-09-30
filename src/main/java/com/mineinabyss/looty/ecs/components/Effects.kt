package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.GearyComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bukkit.potion.PotionEffectType

@Serializable
@SerialName("mobzy:potioneffect")
class PotionComponent (
        @SerialName("effect")
        private val _effect: String,
        val level: Int
):  GearyComponent(){
    @Transient
    val effect = PotionEffectType.getByName(_effect) ?: error("Invalid potion effect $_effect")

    operator fun component1() = effect
    operator fun component2() = level
}