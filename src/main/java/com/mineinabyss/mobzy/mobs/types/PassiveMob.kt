package com.mineinabyss.mobzy.mobs.types

import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.pathfinders.PathfinderGoalWalkingAnimation
import com.mineinabyss.mobzy.registration.MobzyTemplates
import net.minecraft.server.v1_16_R1.*

/**
 * Originally based off EntityPig
 */
abstract class PassiveMob(world: World?, name: String) : EntityAnimal(MobzyTemplates[name].type as EntityTypes<out EntityAnimal>, world), CustomMob {
    //implementation of properties from CustomMob
    override var killedMZ: Boolean
        get() = killed
        set(value) {
            killed = value
        }
    override val entity: EntityLiving get() = this
    override fun lastDamageByPlayerTime(): Int = lastDamageByPlayerTime //TODO I want a consistent fix for the ambiguity errors, this might be it
    override val killScore: Int = aV

    //implementation of behaviours

    override fun createPathfinders() {
        addPathfinderGoal(0, PathfinderGoalWalkingAnimation(living, template.model))
        addPathfinderGoal(1, PathfinderGoalFloat(this))
        addPathfinderGoal(2, PathfinderGoalPanic(this, 1.25))
        addPathfinderGoal(3, PathfinderGoalBreed(this, 1.0))
        addPathfinderGoal(5, PathfinderGoalFollowParent(this, 1.1))
        addPathfinderGoal(6, PathfinderGoalRandomStrollLand(this, 1.0))
        addPathfinderGoal(7, PathfinderGoalLookAtPlayer(this, EntityPlayer::class.java, 6.0f))
    }

    override fun saveMobNBT(nbttagcompound: NBTTagCompound?) = Unit

    override fun loadMobNBT(nbttagcompound: NBTTagCompound?) = Unit

    override fun dropExp() = dropExperience()

    //overriding NMS methods


    open fun eL(): AttributeProvider.Builder? {
        return EntityInsentient.p().a(GenericAttributes.MAX_HEALTH, 10.0).a(GenericAttributes.MOVEMENT_SPEED, 0.25)
    }
//    override fun initAttributes() = super.initAttributes().also { setConfiguredAttributes() }
    override fun initPathfinder() = createPathfinders()

    override fun saveData(nbttagcompound: NBTTagCompound) = super.saveData(nbttagcompound).also { loadMobNBT(nbttagcompound) }
    override fun loadData(nbttagcompound: NBTTagCompound) = super.loadData(nbttagcompound).also { saveMobNBT(nbttagcompound) }

    override fun die(damagesource: DamageSource) = dieCM(damagesource)
    override fun getScoreboardDisplayName() = scoreboardDisplayNameMZ
    override fun getExpValue(entityhuman: EntityHuman): Int = expToDrop()

    override fun getSoundAmbient(): SoundEffect? = null.also { makeSound(soundAmbient) }
    override fun getSoundHurt(damagesource: DamageSource): SoundEffect? = null.also { makeSound(soundHurt) }
    override fun getSoundDeath(): SoundEffect? = null.also { makeSound(soundDeath) }
    override fun a(blockposition: BlockPosition, iblockdata: IBlockData) = makeSound(soundStep)

    //EntityAnimal specific overriding

    override fun createChild(entityageable: EntityAgeable): EntityAgeable? = null

    init {
        createFromBase()
        addScoreboardTag("passiveMob")
        living.removeWhenFarAway = false
    }
}