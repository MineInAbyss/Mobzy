package com.mineinabyss.mobzy.injection

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.papermc.GearyMCContext
import com.mineinabyss.geary.papermc.access.toBukkit
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import net.minecraft.sounds.SoundEvent
import org.bukkit.SoundCategory
import kotlin.random.Random

/**
 * An interface that should be used by all custom mobs that use a custom class within Minecraft's entity
 * hierarchy.
 */
interface CustomEntity {
    companion object {
        const val ENTITY_VERSION = "customMob3"
    }
}

/** Plays a sound effect at the mob's location and returns null */
context(GearyMCContext)
fun NMSEntity.makeSound(default: SoundEvent? = null, sound: Sounds.() -> String?): SoundEvent? {
    val entity = toGeary()
    entity.makeSound(entity.get<Sounds>()?.sound() ?: return default)
    return null
}

//TODO think of a better place to put this, something less inheritance-ey
context(GearyMCContext)
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

context(GearyMCContext)
fun NMSEntity.toGeary(): GearyEntity = toBukkit().toGeary()
