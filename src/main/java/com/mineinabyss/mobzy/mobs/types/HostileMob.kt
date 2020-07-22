package com.mineinabyss.mobzy.mobs.types

import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.pathfinders.PathfinderGoalWalkingAnimation
import com.mineinabyss.mobzy.pathfinders.hostile.PathfinderGoalMeleeAttackPitchLock
import com.mineinabyss.mobzy.registration.MobzyTemplates
import net.minecraft.server.v1_16_R1.*
import java.lang.reflect.Field
import java.lang.reflect.Modifier


/**
 * Lots of code taken from EntityZombie
 */
abstract class HostileMob(world: World?, name: String) : EntityMonster(MobzyTemplates[name].type as EntityTypes<out EntityMonster>, world), CustomMob {

    //implementation of properties from CustomMob
    override var killedMZ: Boolean
        get() = killed
        set(value) {
            killed = value
        }
    override val entity: EntityLiving
        get() = this

    override fun lastDamageByPlayerTime(): Int = lastDamageByPlayerTime
    override val killScore: Int = aV

    //implementation of behaviours

    override fun createPathfinders() {
        addPathfinderGoal(0, PathfinderGoalWalkingAnimation(living, template.model))
        addPathfinderGoal(2, PathfinderGoalMeleeAttack(this, 1.0, false )) //TODO dont force pitch lock but keep pathfinder
        addPathfinderGoal(3, PathfinderGoalFloat(this))
        addPathfinderGoal(7, PathfinderGoalRandomStrollLand(this, 1.0))
        addPathfinderGoal(7, PathfinderGoalLookAtPlayer(this, EntityPlayer::class.java, 8.0f))
        addPathfinderGoal(8, PathfinderGoalRandomLookaround(this))

        addTargetSelector(2, PathfinderGoalNearestAttackableTarget(this, EntityHuman::class.java, true))
    }

    override fun saveMobNBT(nbttagcompound: NBTTagCompound?) = Unit
    override fun loadMobNBT(nbttagcompound: NBTTagCompound?) = Unit

    override fun dropExp() = dropExperience()

    //overriding NMS methods

//    open fun eL(): AttributeProvider.Builder? {
//        return EntityInsentient.p().a(GenericAttributes.MAX_HEALTH, 10.0).a(GenericAttributes.MOVEMENT_SPEED, 0.25)
//    }
//    fun eT() = super.eT().also { setConfiguredAttributes() }

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

    //EntityMonster specific overriding

    /**
     * Removes entity if not in peaceful mode
     */
    override fun tick() = super.tick().also { if (!world.isClientSide && world.difficulty == EnumDifficulty.PEACEFUL) die() }
    init {
        createFromBase()
        addScoreboardTag("hostileMob")
        living.removeWhenFarAway = true
        attributeMap
    }
}