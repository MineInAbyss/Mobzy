package com.mineinabyss.mobzy.mobs

import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.components.addComponent
import com.mineinabyss.geary.ecs.components.get
import com.mineinabyss.geary.ecs.types.GearyEntityType
import com.mineinabyss.geary.minecraft.components.BukkitEntityComponent
import com.mineinabyss.geary.minecraft.store.BukkitEntityAccess
import com.mineinabyss.geary.minecraft.store.decodeComponents
import com.mineinabyss.idofront.events.call
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntity
import com.mineinabyss.mobzy.api.nms.aliases.NMSSound
import com.mineinabyss.mobzy.api.nms.aliases.toBukkit
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import com.mineinabyss.mobzy.ecs.events.MobLoadEvent
import com.mineinabyss.mobzy.registration.MobzyTypes
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
    //TODO this should always be implemented as Engine.getNextId() but we can't init here :(
    override val gearyId: Int

    val nmsEntity: NMSEntity
    val entity get() = nmsEntity.toBukkit()

    override fun getPersistentDataContainer(): PersistentDataContainer = entity.persistentDataContainer

    /**
     * Applies some default attributes that every custom mob should have, such as a model, invisibility, and an
     * identifier scoreboard tag
     */
    fun initMob() {
        val type = MobzyTypes[this]
        addComponent<GearyEntityType>(type)

        //adding components from the type to this entity
        decodeComponents()
        addComponent(BukkitEntityComponent(entity.uniqueId, entity))

        //allow us to get the geary entity via UUID
        BukkitEntityAccess.registerEntity(entity, this)

        //the number is literally just for migrations. Once we figure out how we do that for ecs components, we should
        // use the same system here.
        entity.addScoreboardTag("customMob3")

        MobLoadEvent(this).call()
    }

    //TODO think of a better place to put this, something less inheritance-ey
    fun makeSound(sound: String) {
        val sounds = get<Sounds>()
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
        makeSound(get<Sounds>()?.sound() ?: return default)
        return null
    }
}
