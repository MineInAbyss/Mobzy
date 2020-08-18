package com.mineinabyss.mobzy.mobs

import com.mineinabyss.mobzy.api.nms.aliases.NMSDataContainer
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityInsentient
import com.mineinabyss.mobzy.api.nms.aliases.toBukkit
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.addComponent
import com.mineinabyss.mobzy.ecs.components.minecraft.MobComponent
import com.mineinabyss.mobzy.ecs.events.EntityCreatedEvent
import net.minecraft.server.v1_16_R1.ChatMessage
import net.minecraft.server.v1_16_R1.EntityHuman
import net.minecraft.server.v1_16_R1.EntityInsentient
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

    val locX get() = entity.location.x
    val locY get() = entity.location.y
    val locZ get() = entity.location.z
    val killer: EntityHuman? get() = nmsEntity.killer

    val scoreboardDisplayNameMZ: ChatMessage get() = ChatMessage(type.name.split('_').joinToString(" ") { it.capitalize() })

    var target
        get() = nmsEntity.goalTarget?.toBukkit()
        set(value) {
            nmsEntity.goalTarget = value?.toNMS<NMSEntityInsentient>()
        }

    // ========== Things to be implemented ==========
    val soundAmbient: String? get() = null
    val soundHurt: String? get() = null
    val soundDeath: String? get() = null
    val soundStep: String? get() = null
    var dead: Boolean
    val killScore: Int

    fun createPathfinders()
    fun lastDamageByPlayerTime(): Int
    fun saveMobNBT(nbttagcompound: NMSDataContainer) = Unit
    fun loadMobNBT(nbttagcompound: NMSDataContainer) = Unit
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
        type.staticComponents.forEach { (_, component) ->
            addComponent(component)
        }
        type.components.forEach { (_, component) ->
            addComponent(component.copy())
        }
    }

    @Suppress("UNREACHABLE_CODE")
    fun makeSound(sound: String?) {
        if (sound != null)
            entity.world.playSound(entity.location, sound, SoundCategory.NEUTRAL, 1f, (Random.nextDouble(1.0, 1.02).toFloat()))
    }

    // ========== Helper methods ===================

    fun randomSound(vararg sounds: String) = sounds.random()
}