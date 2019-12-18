package com.offz.spigot.mobzy.mobs.types

import com.offz.spigot.mobzy.CustomType.Companion.getTemplate
import com.offz.spigot.mobzy.CustomType.Companion.getType
import com.offz.spigot.mobzy.mobs.CustomMob
import com.offz.spigot.mobzy.mobs.MobTemplate
import com.offz.spigot.mobzy.mobs.behaviours.CustomMobBase
import com.offz.spigot.mobzy.mobs.behaviours.Disguiseable
import com.offz.spigot.mobzy.mobs.behaviours.ExpDroppable
import com.offz.spigot.mobzy.mobs.behaviours.InitAttributeable
import net.minecraft.server.v1_15_R1.*
import org.bukkit.entity.LivingEntity

/**
 * Lots of code taken from EntityPig
 */
abstract class PassiveMob(world: World?, override var builder: MobTemplate) : EntityAnimal(getType(builder) as EntityTypes<out EntityAnimal>, world), CustomMob {
    private val disguiseable = Disguiseable(this)

    constructor(world: World?, name: String?) : this(world, getTemplate(name!!))

    override fun getExpValue(entityhuman: EntityHuman): Int {
        val toDrop = ExpDroppable(this).expToDrop
        return toDrop ?: super.getExpValue(entityhuman)
    }

    override val entity: EntityLiving
        get() = this

    override fun initPathfinder() {
        createPathfinders()
    }

    override val soundAmbient: String? = null
    override val soundDeath: String? = null
    override val soundHurt: String? = null
    override val soundStep: String? = null

    //TODO fix getting sound
    override fun getSoundAmbient(): SoundEffect? {
        if(soundAmbient == null) return null
        living.world.playSound(living.location, soundAmbient!!, org.bukkit.SoundCategory.NEUTRAL, 1f, (1 + Math.random() * 0.2).toFloat())
        return null
    }

    override fun getSoundHurt(damagesource: DamageSource): SoundEffect? {
        if(soundHurt == null) return null
        living.world.playSound(living.location, soundHurt!!, org.bukkit.SoundCategory.NEUTRAL, 1f, (1 + Math.random() * 0.2).toFloat())
        return null
    }

    override fun getSoundDeath(): SoundEffect? {
        if(soundDeath == null) return null
        living.world.playSound(living.location, soundDeath!!, org.bukkit.SoundCategory.NEUTRAL, 1f, (1 + Math.random() * 0.2).toFloat())
        return null
    }

    override fun a(blockposition: BlockPosition, iblockdata: IBlockData) {
        if(soundStep == null) return
        living.world.playSound(living.location, soundStep!!, org.bukkit.SoundCategory.NEUTRAL, 1f, (1 + Math.random() * 0.2).toFloat())
        return
    }

    override fun createPathfinders() {
        goalSelector.a(1, PathfinderGoalFloat(this))
        goalSelector.a(2, PathfinderGoalPanic(this, 1.25))
        goalSelector.a(3, PathfinderGoalBreed(this, 1.0))
        goalSelector.a(5, PathfinderGoalFollowParent(this, 1.1))
        goalSelector.a(6, PathfinderGoalRandomStrollLand(this, 1.0))
        //        goalSelector.a(7, new PathfinderGoalLookAtPlayerPitchLock(this, EntityTypes.PLAYER, 6.0F));
//        goalSelector.a(8, new PathfinderGoalLookWhereHeaded(this));
    }

    override fun initAttributes() {
        super.initAttributes()
        val initAttributeable = InitAttributeable(this)
        initAttributeable.setConfiguredAttributes()
    }

    override var killed: Boolean = false

    override fun lastDamageByPlayerTime(): Int {
        return lastDamageByPlayerTime
    }

    //FIXME
    override val killScore: Int
        get() = 0 //FIXME


    override fun b(nbttagcompound: NBTTagCompound) {
        super.b(nbttagcompound)
        saveMobNBT(nbttagcompound)
    }

    override fun saveMobNBT(nbttagcompound: NBTTagCompound?) { //TODO make the mobs undisguise when unloaded, not just on death. It should ONLY happen when the chunk the mob is in gets unloaded,
// and this method gets called randomly sometimes. Perhaps a ChunkUnloadEvent or something similar is a good way to do this
    }

    override fun a(nbttagcompound: NBTTagCompound) {
        super.a(nbttagcompound)
        loadMobNBT(nbttagcompound)
    }

    override fun loadMobNBT(nbttagcompound: NBTTagCompound?) {
        disguiseable.disguise()
    }

    override fun die(damagesource: DamageSource) {
        dieCM(damagesource)
    }

    override fun die() {
        super.die()
        disguiseable.undisguise()
    }

    override fun createChild(entityageable: EntityAgeable): EntityAgeable? {
        return null
    }

    override fun getScoreboardDisplayName(): IChatBaseComponent {
        return ChatMessage(builder.name)
    }

    init {
        val base = CustomMobBase(this)
        base.apply()
        addScoreboardTag("passiveMob")
        //TODO this is a temporary fix to see if it affects performance
        (bukkitEntity as LivingEntity).removeWhenFarAway = true
    }
}