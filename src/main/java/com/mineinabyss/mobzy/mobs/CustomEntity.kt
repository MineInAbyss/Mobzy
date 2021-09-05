package com.mineinabyss.mobzy.mobs

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.minecraft.access.toBukkit
import com.mineinabyss.geary.minecraft.access.toGeary
import com.mineinabyss.idofront.nms.aliases.NMSEntity
import com.mineinabyss.idofront.nms.aliases.NMSSound
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import org.bukkit.SoundCategory
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataHolder
import kotlin.random.Random

/**
 * A class for linking Bukkit entities to Geary entities. This is meant for Mobzy's own custom entities to use to
 * share common code. We can't use an abstract class yet since we need our entities to extend existing NMS ones.
 *
 * @property id The ID within Geary's ECS for this entity.
 * This should always be implemented as `Engine.getNextId()`.
 * @property nmsEntity The NMS equivalent of this geary entity.
 * @property entity The bukkit equivalent of this geary entity.
 *
 * @see CustomMob
 */
interface CustomEntity : PersistentDataHolder {
    //TODO try to get geary entity without going thru hashmap every time

    val nmsEntity: NMSEntity
    val entity get() = nmsEntity.toBukkit()

    override fun getPersistentDataContainer(): PersistentDataContainer = entity.persistentDataContainer

    companion object {
        const val ENTITY_VERSION = "customMob3"
    }
}

/** Plays a sound effect at the mob's location and returns null */
fun NMSEntity.makeSound(default: NMSSound? = null, sound: Sounds.() -> String?): NMSSound? {
    val entity = geary
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

val NMSEntity.geary: GearyEntity get() = toBukkit().toGeary()
