package com.mineinabyss.mobzy.mobs.types

import com.mineinabyss.geary.minecraft.access.geary
import com.mineinabyss.geary.minecraft.store.encodeComponents
import com.mineinabyss.idofront.nms.aliases.*
import com.mineinabyss.idofront.nms.entity.typeName
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import com.mineinabyss.mobzy.ecs.components.death.DeathLoot
import com.mineinabyss.mobzy.mobs.CustomMob
import org.bukkit.entity.HumanEntity
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

    override val killScore: Int get() = aO
    override fun dropExp() = dropExperience()

    //TODO option to inherit pathfinders from a group
    final override fun initPathfinder() = createPathfinders()
    override fun createPathfinders() = super.initPathfinder()

    final override fun saveData(nbttagcompound: NMSDataContainer) {
        encodeComponents(geary(entity).getPersistingComponents())
        super.saveData(nbttagcompound)
    }

    final override fun loadData(nbttagcompound: NMSDataContainer) {
        //TODO Not sure if we should load components here considering they already get loaded on init
//        decodeComponents()
        super.loadData(nbttagcompound)
    }

    final override fun b(entityhuman: NMSEntityHuman, enumhand: NMSHand): NMSInteractionResult =
        onPlayerInteract(entityhuman.toBukkit(), enumhand)

    override fun onPlayerInteract(player: HumanEntity, enumhand: NMSHand): NMSInteractionResult =
        super.b(player.toNMS(), enumhand)

    override fun die(damagesource: NMSDamageSource) = dieCustom(damagesource)

    private val scoreboardDisplayName =
        //TODO make sure this is properly formatting entityType name
        NMSChatMessage(nmsEntity.entityType.typeName.split('_').joinToString(" ") { it.capitalize() })

    override fun getScoreboardDisplayName() = scoreboardDisplayName

    override fun getExpValue(entityhuman: NMSEntityHuman): Int =
        geary(entity).get<DeathLoot>()?.expToDrop() ?: this.expToDrop

    override fun getSoundVolume(): Float = geary(entity).get<Sounds>()?.volume ?: super.getSoundVolume()
    override fun getSoundAmbient(): NMSSound? = makeSound(super.getSoundAmbient()) { ambient }
    override fun getSoundDeath(): NMSSound? = makeSound(super.getSoundDeath()) { death }
    override fun getSoundSplash(): NMSSound? = makeSound(super.getSoundSplash()) { splash }
    override fun getSoundSwim(): NMSSound? = makeSound(super.getSoundSwim()) { swim }
    override fun getSoundHurt(damagesource: NMSDamageSource): NMSSound? =
        makeSound(super.getSoundHurt(damagesource)) { hurt }
}
