package com.mineinabyss.mobzy.mobs

import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.components.addComponent
import com.mineinabyss.geary.ecs.components.get
import com.mineinabyss.geary.ecs.types.GearyEntityType
import com.mineinabyss.geary.minecraft.components.MobComponent
import com.mineinabyss.geary.minecraft.store.BukkitEntityAccess
import com.mineinabyss.geary.minecraft.store.decodeComponents
import com.mineinabyss.idofront.events.call
import com.mineinabyss.mobzy.api.nms.aliases.*
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import com.mineinabyss.mobzy.ecs.components.initialization.Model
import com.mineinabyss.mobzy.ecs.events.MobLoadEvent
import org.bukkit.SoundCategory
import org.bukkit.entity.HumanEntity
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataHolder
import kotlin.random.Random

/**
 * @property killScore The score with which a player should be rewarded with when the current entity is killed.
 * @property killer The killer of the current entity if it has one.
 * @property scoreboardDisplayNameMZ Used to change the name displayed in the death message.
 * @property type This entity types's [MobType] describing information about entities of this time. It is
 * immutable and not unique to this specific entity.
 */
interface CustomMob : GearyEntity, PersistentDataHolder {
    override val gearyId: Int

    // ========== Useful properties ===============
    //TODO Use NMSEntity instead, access insentient via a MobComponent
    val nmsEntity: NMSEntityInsentient

    @Suppress("UNCHECKED_CAST")
    val entity
        get() = nmsEntity.toBukkit()

    override fun getPersistentDataContainer(): PersistentDataContainer = entity.persistentDataContainer

    val type: MobType

    val killer: NMSEntityHuman? get() = nmsEntity.killer

    val scoreboardDisplayNameMZ: NMSChatMessage
        get() = NMSChatMessage(
            type.name.split('_').joinToString(" ") { it.capitalize() })

    var target
        get() = nmsEntity.goalTarget?.toBukkit()
        set(value) {
            nmsEntity.goalTarget = value?.toNMS<NMSEntityInsentient>()
        }

    // ========== Things to be implemented ==========
    var dead: Boolean
    val killScore: Int

    fun createPathfinders()

    fun lastDamageByPlayerTime(): Int

    fun saveMobNBT(nbttagcompound: NMSDataContainer)
    fun loadMobNBT(nbttagcompound: NMSDataContainer)

    fun dropExp()

    fun onPlayerInteract(player: HumanEntity, enumhand: NMSHand): NMSInteractionResult

    // ========== Pre-written behaviour ============
    /**
     * Applies some default attributes that every custom mob should have, such as a model, invisibility, and an
     * identifier scoreboard tag
     */
    fun initMob() {

        //save the entity type's name under scoreboard tags so we can identify this entity's type even if it's no longer
        // considered an instance of CustomMob (ex. after a plugin reload).

        //add type under the generic component since getting components doesn't
        addComponent<GearyEntityType>(type)
        //adding components from the type to this entity
        decodeComponents()
        addComponent(MobComponent(entity.uniqueId, entity))

        //allow us to get the geary entity via UUID
        BukkitEntityAccess.registerEntity(entity, this)

        //the number is literally just for migrations. Once we figure out how we do that for ecs components, we should
        // use the same system here.
        entity.addScoreboardTag("customMob3")
        entity.addScoreboardTag(type.name)

        MobLoadEvent(this).call()

        if (get<Model>()?.small == true) entity.toNMS().isBaby = true
    }

    fun makeSound(sound: String?) {
        val sounds = get<Sounds>()
        val volume = sounds?.volume ?: 1.0f
        val pitch = sounds?.pitch ?: 1.0
        val pitchRange = sounds?.pitchRange ?: 0.2
        if (sound != null)
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
