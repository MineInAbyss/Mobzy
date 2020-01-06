package com.offz.spigot.mobzy.mobs.types

import com.offz.spigot.mobzy.mobs.CustomMob
import com.offz.spigot.mobzy.mobs.MobTemplate
import com.offz.spigot.mobzy.pathfinders.controllers.MZControllerMoveFlying
import com.offz.spigot.mobzy.pathfinders.flying.PathfinderGoalFlyDamageTarget
import com.offz.spigot.mobzy.pathfinders.flying.PathfinderGoalIdleFly
import com.offz.spigot.mobzy.toTemplate
import com.offz.spigot.mobzy.type
import net.minecraft.server.v1_15_R1.*

/**
 * Lots of code taken from the EntityGhast class for flying mobs
 */
abstract class FlyingMob(world: World?, override var template: MobTemplate) : EntityFlying(template.type as EntityTypes<out EntityFlying>, world), CustomMob {
    constructor(world: World?, name: String) : this(world, name.toTemplate())

    //implementation of properties from CustomMob
    override var killedMZ: Boolean
        get() = killed
        set(value) {
            killed = value
        }
    override val entity: EntityLiving
        get() = this

    override fun lastDamageByPlayerTime(): Int = lastDamageByPlayerTime
    override val killScore: Int = aW

    //implementation of behaviours
    
    override fun createPathfinders() {
        addPathfinderGoal(1, PathfinderGoalFloat(this))
        addPathfinderGoal(1, PathfinderGoalFlyDamageTarget(this))
        addPathfinderGoal(5, PathfinderGoalIdleFly(this))
        addTargetSelector(1, PathfinderGoalNearestAttackableTarget(this, EntityHuman::class.java, true))
    }

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

    //EntityFlying specific overriding

    init {
        createFromBase()
        addScoreboardTag("flyingMob")
        living.removeWhenFarAway = true
        moveController = MZControllerMoveFlying(this)
    }
}