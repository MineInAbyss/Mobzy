package com.offz.spigot.mobzy.mobs.types

import com.offz.spigot.mobzy.mobs.CustomMob
import com.offz.spigot.mobzy.mobs.MobTemplate
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalLookAtPlayerPitchLock
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalWalkingAnimation
import com.offz.spigot.mobzy.toTemplate
import com.offz.spigot.mobzy.type
import net.minecraft.server.v1_15_R1.*

/**
 * Originally based off EntityPig
 */
abstract class PassiveMob(world: World?, override var template: MobTemplate) : EntityAnimal(template.type as EntityTypes<out EntityAnimal>, world), CustomMob {
    constructor(world: World?, name: String) : this(world, name.toTemplate())

    //implementation of properties from CustomMob
    override var killedMZ: Boolean
        get() = killed
        set(value) {
            killed = value
        }
    override val entity: EntityLiving
        get() = this

    override fun lastDamageByPlayerTime(): Int = lastDamageByPlayerTime //TODO I want a consistent fix for the ambiguity errors, this might be it
    override val killScore: Int = aW

    //implementation of behaviours

    override fun createPathfinders() {
        addPathfinderGoal(0, PathfinderGoalWalkingAnimation(living, staticTemplate.modelID))
        addPathfinderGoal(1, PathfinderGoalFloat(this))
        addPathfinderGoal(2, PathfinderGoalPanic(this, 1.25))
        addPathfinderGoal(3, PathfinderGoalBreed(this, 1.0))
        addPathfinderGoal(5, PathfinderGoalFollowParent(this, 1.1))
        addPathfinderGoal(6, PathfinderGoalLookAtPlayerPitchLock(this, EntityTypes.PLAYER, 6.0, 0.02f))
        addPathfinderGoal(7, PathfinderGoalRandomStrollLand(this, 1.0))
    }

    /**
     * TODO make the mobs undisguise when unloaded, not just on death. It should ONLY happen when the chunk the mob is in gets unloaded,
     *  and this method gets called randomly sometimes. Perhaps a ChunkUnloadEvent or something similar is a good way to do this
     */
    override fun saveMobNBT(nbttagcompound: NBTTagCompound?) = Unit

    override fun loadMobNBT(nbttagcompound: NBTTagCompound?) = disguise()

    override fun dropExp() = dropExperience()

    //overriding NMS methods

    override fun initAttributes() = super.initAttributes().also { setConfiguredAttributes() }
    override fun initPathfinder() = createPathfinders()

    override fun a(nbttagcompound: NBTTagCompound) = super.a(nbttagcompound).also { loadMobNBT(nbttagcompound) }
    override fun b(nbttagcompound: NBTTagCompound) = super.b(nbttagcompound).also { saveMobNBT(nbttagcompound) }

    override fun die() = super.die().also { undisguise() }
    override fun die(damagesource: DamageSource) = dieCM(damagesource)
    override fun getScoreboardDisplayName() = scoreboardDisplayNameMZ
    override fun getExpValue(entityhuman: EntityHuman): Int = expToDrop()

    override fun getSoundAmbient(): SoundEffect? = null.also { makeSound(soundAmbient) }
    override fun getSoundHurt(damagesource: DamageSource): SoundEffect? = null.also { makeSound(soundHurt) }
    override fun getSoundDeath(): SoundEffect? = null.also { makeSound(soundDeath) }
    override fun a(blockposition: BlockPosition, iblockdata: IBlockData) = makeSound(soundStep)
//

    //EntityAnimal specific overriding

    override fun createChild(entityageable: EntityAgeable): EntityAgeable? = null

    init {
        createFromBase()
        addScoreboardTag("passiveMob")
        //TODO this is a temporary fix to see if it affects performance
        living.removeWhenFarAway = true
    }
}