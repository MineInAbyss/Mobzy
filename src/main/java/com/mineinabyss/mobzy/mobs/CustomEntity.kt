package com.mineinabyss.mobzy.mobs

import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.GearyEntityId
import com.mineinabyss.geary.minecraft.store.geary
import com.mineinabyss.geary.minecraft.store.get
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSSound
import com.mineinabyss.mobzy.api.nms.aliases.toBukkit
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import org.bukkit.SoundCategory
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataHolder
import kotlin.random.Random

/**
 * A class for linking Bukkit entities to Geary entities. This is meant for Mobzy's own custom entities to use to
 * share common code. We can't use an abstract class yet since we need our entities to extend existing NMS ones.
 *
 * @property gearyId The ID within Geary's ECS for this entity.
 * This should always be implemented as `Engine.getNextId()`.
 * @property nmsEntity The NMS equivalent of this geary entity.
 * @property entity The bukkit equivalent of this geary entity.
 *
 * @see CustomMob
 */
interface CustomEntity : GearyEntity, PersistentDataHolder {
    //TODO try to do this without getting the id via entity every time
    override val gearyId: GearyEntityId get() = geary(entity).gearyId

    val nmsEntity: NMSEntity
    val entity get() = nmsEntity.toBukkit()

    override fun getPersistentDataContainer(): PersistentDataContainer = entity.persistentDataContainer

    //TODO think of a better place to put this, something less inheritance-ey
    fun makeSound(sound: String) {
        val sounds = entity.get<Sounds>()
        val volume = sounds?.volume ?: 1.0f
        val pitch = sounds?.pitch ?: 1.0
        val pitchRange = sounds?.pitchRange ?: 0.2

        entity.world.playSound(
            entity.location,
            sound,
            SoundCategory.NEUTRAL,
            volume,
            (pitch + (Random.nextDouble(-pitchRange, pitchRange))).toFloat()
        )
    }

    /** Plays a sound effect at the mob's location and returns null */
    fun makeSound(default: NMSSound? = null, sound: Sounds.() -> String?): NMSSound? {
        makeSound(entity.get<Sounds>()?.sound() ?: return default)
        return null
    }

    companion object {
        const val ENTITY_VERSION = "customMob3"
    }
}
