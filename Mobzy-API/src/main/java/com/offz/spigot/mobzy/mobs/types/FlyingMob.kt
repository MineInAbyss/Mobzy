package com.offz.spigot.mobzy.mobs.types

import com.offz.spigot.mobzy.CustomType
import com.offz.spigot.mobzy.mobs.CustomMob
import com.offz.spigot.mobzy.mobs.MobTemplate
import net.minecraft.server.v1_15_R1.*
import org.bukkit.entity.LivingEntity

/**
 * Lots of code taken from the EntityGhast class for flying mobs
 */
abstract class FlyingMob(world: World?, override var template: MobTemplate) : EntityFlying(CustomType.getType(template) as EntityTypes<out EntityFlying>, world), CustomMob {
    constructor(world: World?, name: String?) : this(world, CustomType.getTemplate(name!!))

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
        addPathfinderGoal(1, PathfinderGoalFloat(this))
//        addPathfinderGoal(1, PathfinderGoalFlyDamageTarget(this));
//        addPathfinderGoal(5, PathfinderGoalIdleFly(this));
//        addTargetSelector(1, PathfinderGoalTargetNearestPlayer(this));
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
    override fun getScoreboardDisplayName(): ChatMessage = ChatMessage(template.name) //TODO I forget why I did this, maybe to change the death message
//    override fun getExpValue(entityhuman: EntityHuman): Int = expToDrop().also { debug(expToDrop().toString()) } //TODO exp isnt dropping

    override fun getSoundAmbient(): SoundEffect? = null.also { makeSound(soundAmbient) }
    override fun getSoundHurt(damagesource: DamageSource): SoundEffect? = null.also { makeSound(soundHurt) }
    override fun getSoundDeath(): SoundEffect? = null.also { makeSound(soundDeath) }
    override fun a(blockposition: BlockPosition, iblockdata: IBlockData) = makeSound(soundStep)
//

    //EntityFlying specific overriding

    /**
     * Removes entity if not in peaceful mode
     */
    override fun tick() = super.tick().also { if (!world.isClientSide && world.difficulty == EnumDifficulty.PEACEFUL) die() }

    init {
        createFromBase()
        addScoreboardTag("passiveMob")
        //TODO this is a temporary fix to see if it affects performance
        (bukkitEntity as LivingEntity).removeWhenFarAway = true
    }
}