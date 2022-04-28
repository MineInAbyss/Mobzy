package com.mineinabyss.mobzy.injection.helpers

import com.mineinabyss.geary.datatypes.GearyEntity
import com.mineinabyss.geary.papermc.access.toBukkit
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import net.minecraft.sounds.SoundEvent
import org.bukkit.SoundCategory
import kotlin.random.Random

/** Plays a sound effect at the mob's location and returns null */
fun NMSEntity.makeSound(default: SoundEvent? = null, sound: Sounds.() -> String?): SoundEvent? {
    val entity = toGeary()
    entity.makeSound(entity.get<Sounds>()?.sound() ?: return default)
    return null
}

//TODO think of a better place to put this, something less inheritance-ey
fun GearyEntity.makeSound(sound: String) {
    val bukkit = toBukkit() ?: return
    val sounds = get() ?: Sounds()

    bukkit.world.playSound(
        bukkit.location,
        sound,
        SoundCategory.NEUTRAL,
        sounds.volume,
        (sounds.pitch + (Random.nextDouble(-sounds.pitchRange, sounds.pitchRange))).toFloat()
    )
}

fun NMSEntity.toGeary(): GearyEntity = toBukkit().toGeary()
