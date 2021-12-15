package com.mineinabyss.mobzy.ecs.components.ambient

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Component for overriding custom mobs sounds.
 *
 * @param volume How loud the sound is.
 * @param pitch The frequency of the sounds.
 * @param pitchRange When playing a sound, the pitch will a random number between [pitch] +/- [pitchRange].
 * @param ambient The ambient sound this entity should make at random intervals.
 * @param death The sound that plays on death.
 * @param hurt The sound for when getting hit.
 * @param splash The sound when falling into water.
 * @param swim The sound for swimming in water.
 */
@Serializable
@SerialName("mobzy:sounds")
class Sounds(
    val volume: Float = 1F,
    val pitch: Double = 1.0,
    val pitchRange: Double = 0.2,
    val ambient: String? = null,
    val death: String? = null,
    val hurt: String? = null,
    val splash: String? = null,
    val swim: String? = null,
)
