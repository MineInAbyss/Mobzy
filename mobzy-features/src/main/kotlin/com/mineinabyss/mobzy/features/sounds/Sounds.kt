package com.mineinabyss.mobzy.features.sounds

import com.mineinabyss.idofront.serialization.DurationSerializer
import com.mineinabyss.idofront.time.ticks
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.SoundCategory
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

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
 * @param ambientRate How often on average should the ambient sound play.
 */
@Serializable
@SerialName("mobzy:sounds")
class Sounds(
    val step: Sound? = null,
    val ambient: Sound? = null,
    val death: Sound? = null,
    val hurt: Sound? = null,
    val splash: Sound? = null,
    val swim: Sound? = null,
    @Serializable(with = DurationSerializer::class)
    val ambientRate: Duration = 15.seconds,
) {
    val ambientChance: Double = 1.ticks / ambientRate

    @Serializable
    @SerialName("mobzy:sound")
    class Sound(
        val sound: String,
        val volume: Float = 1F,
        val pitch: Double = 1.0,
        val pitchRange: Double = 0.2,
        val category: SoundCategory = SoundCategory.MASTER
    ) {
        fun adjustedPitch() = (pitch + (Random.nextDouble(-pitchRange, pitchRange))).toFloat()
    }
}
