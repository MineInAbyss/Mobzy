package com.mineinabyss.mobzy.mobs.types

import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.mobs.MobTemplate
import com.mineinabyss.mobzy.pathfinders.PathfinderGoalLookAtPlayerPitchLock
import com.mineinabyss.mobzy.pathfinders.PathfinderGoalWalkingAnimation
import com.mineinabyss.mobzy.pathfinders.hostile.PathfinderGoalMeleeAttackPitchLock
import com.mineinabyss.mobzy.toTemplate
import com.mineinabyss.mobzy.type
import net.minecraft.server.v1_15_R1.*

/**
 * Lots of code taken from EntityZombie
 */
abstract class HostileMob(world: World?, override var template: MobTemplate) : EntityMonster(template.type as EntityTypes<out EntityMonster>, world), CustomMob {
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
        addPathfinderGoal(0, PathfinderGoalWalkingAnimation(living, staticTemplate.model))
        addPathfinderGoal(1, PathfinderGoalFloat(this))
        addPathfinderGoal(2, PathfinderGoalMeleeAttackPitchLock(this))
        addPathfinderGoal(7, PathfinderGoalRandomStrollLand(this, 1.0))
        addPathfinderGoal(8, PathfinderGoalLookAtPlayerPitchLock(this, EntityTypes.PLAYER, 8.0))
        addPathfinderGoal(8, PathfinderGoalRandomLookaround(this))
        addTargetSelector(2, PathfinderGoalNearestAttackableTarget(this, EntityHuman::class.java, true))
    }

    override fun saveMobNBT(nbttagcompound: NBTTagCompound?) = Unit
    override fun loadMobNBT(nbttagcompound: NBTTagCompound?) = Unit

    override fun dropExp() = dropExperience()

    //overriding NMS methods

    override fun initAttributes() = super.initAttributes().also { setConfiguredAttributes() }
    override fun initPathfinder() = createPathfinders()

    override fun a(nbttagcompound: NBTTagCompound) = super.a(nbttagcompound).also { loadMobNBT(nbttagcompound) }
    override fun b(nbttagcompound: NBTTagCompound) = super.b(nbttagcompound).also { saveMobNBT(nbttagcompound) }

    override fun die(damagesource: DamageSource) = dieCM(damagesource)
    override fun getScoreboardDisplayName() = scoreboardDisplayNameMZ
    override fun getExpValue(entityhuman: EntityHuman): Int = expToDrop()

    override fun getSoundAmbient(): SoundEffect? = null.also { makeSound(soundAmbient) }
    override fun getSoundHurt(damagesource: DamageSource): SoundEffect? = null.also { makeSound(soundHurt) }
    override fun getSoundDeath(): SoundEffect? = null.also { makeSound(soundDeath) }
    override fun a(blockposition: BlockPosition, iblockdata: IBlockData) = makeSound(soundStep)
//

    //EntityMonster specific overriding

    /**
     * Removes entity if not in peaceful mode
     */
    override fun tick() = super.tick().also { if (!world.isClientSide && world.difficulty == EnumDifficulty.PEACEFUL) die() }

    init {
        createFromBase()
        addScoreboardTag("hostileMob")
        living.removeWhenFarAway = true
    }
}