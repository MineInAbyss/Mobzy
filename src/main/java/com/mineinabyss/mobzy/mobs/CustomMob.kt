package com.mineinabyss.mobzy.mobs

import com.mineinabyss.geary.ecs.Engine
import com.mineinabyss.idofront.events.call
import com.mineinabyss.mobzy.api.nms.aliases.NMSDataContainer
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityInsentient
import com.mineinabyss.mobzy.api.nms.aliases.toBukkit
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.MobComponent
import com.mineinabyss.mobzy.ecs.components.addComponent
import com.mineinabyss.mobzy.ecs.components.addComponents
import com.mineinabyss.mobzy.ecs.components.get
import com.mineinabyss.mobzy.ecs.components.initialization.Model
import com.mineinabyss.mobzy.ecs.events.EntityLoadedEvent
import com.mineinabyss.mobzy.ecs.store.decodeComponents
import com.mineinabyss.mobzy.ecs.store.encodeComponents
import net.minecraft.server.v1_16_R2.ChatMessage
import net.minecraft.server.v1_16_R2.EntityHuman
import net.minecraft.server.v1_16_R2.EntityInsentient
import org.bukkit.SoundCategory
import kotlin.random.Random

/**
 * @property killScore The score with which a player should be rewarded with when the current entity is killed.
 * @property killer The killer of the current entity if it has one.
 * @property scoreboardDisplayNameMZ Used to change the name displayed in the death message.
 * @property type This entity types's [MobType] describing information about entities of this time. It is
 * immutable and not unique to this specific entity.
 */
interface CustomMob {
    val mobzyId: Int

    // ========== Useful properties ===============
    val nmsEntity: EntityInsentient

    @Suppress("UNCHECKED_CAST")
    val entity
        get() = nmsEntity.toBukkit()

    val type: MobType

    val killer: EntityHuman? get() = nmsEntity.killer

    val scoreboardDisplayNameMZ: ChatMessage get() = ChatMessage(type.name.split('_').joinToString(" ") { it.capitalize() })

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

    fun saveMobNBT(nbttagcompound: NMSDataContainer) {
        entity.persistentDataContainer.encodeComponents(Engine.getComponentsFor(mobzyId).filter { it.persist })
    }

    fun loadMobNBT(nbttagcompound: NMSDataContainer) {
        addComponents(entity.persistentDataContainer.decodeComponents())
    }

    fun dropExp()

    // ========== Pre-written behaviour ============
    /**
     * Applies some default attributes that every custom mob should have, such as a model, invisibility, and an
     * identifier scoreboard tag
     */
    fun initMob() {
        entity.addScoreboardTag("customMob3")
        entity.addScoreboardTag(type.name)

        addComponent(MobComponent(entity))
        val existingComponents = entity.persistentDataContainer.decodeComponents()
        addComponents(type.instantiateComponents(existingComponents))
        EntityLoadedEvent(mobzyId).call()

        if (get<Model>()?.small == true) entity.toNMS().isBaby = true
    }

    @Suppress("UNREACHABLE_CODE")
    fun makeSound(sound: String?) {
        if (sound != null)
            entity.world.playSound(entity.location, sound, SoundCategory.NEUTRAL, 1f, (Random.nextDouble(1.0, 1.02).toFloat()))
    }

    // ========== Helper methods ===================

    fun randomSound(vararg sounds: String) = sounds.random()
}