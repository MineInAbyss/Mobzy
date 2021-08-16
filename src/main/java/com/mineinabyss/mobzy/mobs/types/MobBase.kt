package com.mineinabyss.mobzy.mobs.types

import com.mineinabyss.geary.minecraft.access.geary
import com.mineinabyss.idofront.nms.aliases.*
import com.mineinabyss.idofront.nms.entity.typeName
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import com.mineinabyss.mobzy.mobs.CustomEntity
import com.mineinabyss.mobzy.mobs.CustomMob
import org.bukkit.entity.Mob

/**
 * With the help of an annotation processor, our custom mob classes may request to generate a class which extends
 * a specific NMS enitty class (ex EntityFlying), and provides the exact implementation of [CustomMob] as seen below.
 * Our classes may then extend the generated class and have everything they need overriden for them.
 *
 * This is essentially our solution to not being able to be a subclass of two classes at once. We can't make this class
 * an interface since we wish to initialize variables here, as well as override protected functions.
 *
 * @property scoreboardDisplayName Used to change the name displayed in the death message.
 */
abstract class MobBase : NMSEntityInsentient(error(""), error("")), CustomMob {
    final override val nmsEntity: NMSEntityInsentient get() = this
    final override val entity: Mob get() = this.toBukkit()

    override val killScore: Int get() = bl //TODO unsure if this is it

    //TODO option to inherit pathfinders from a group
    final override fun initPathfinder() = createPathfinders()
    override fun createPathfinders() = super.initPathfinder()

    private val scoreboardDisplayName =
        //TODO make sure this is properly formatting entityType name
        NMSChatMessage(nmsEntity.entityType.typeName.split('_').joinToString(" ") { it.capitalize() })

    //TODO if we can override the name in the entity type, this will read it from there
    override fun getScoreboardDisplayName() = scoreboardDisplayName

    override fun getSoundVolume(): Float = geary(entity).get<Sounds>()?.volume ?: super.getSoundVolume()
    override fun getSoundAmbient(): NMSSound? = makeSound(super.getSoundAmbient()) { ambient }
    override fun getSoundDeath(): NMSSound? = makeSound(super.getSoundDeath()) { death }
    override fun getSoundSplash(): NMSSound? = makeSound(super.getSoundSplash()) { splash }
    override fun getSoundSwim(): NMSSound? = makeSound(super.getSoundSwim()) { swim }
    override fun getSoundHurt(damagesource: NMSDamageSource): NMSSound? =
        makeSound(super.getSoundHurt(damagesource)) { hurt }

    init {
        entity.removeWhenFarAway = false
        entity.addScoreboardTag(CustomEntity.ENTITY_VERSION)
    }
}
