package com.mineinabyss.mobzy.mobs.types

import com.mineinabyss.mobzy.api.nms.aliases.NMSDataContainer
import com.mineinabyss.mobzy.mobs.CustomMob
import com.mieninabyss.mobzy.processor.CustomMobOverrides
import net.minecraft.server.v1_16_R1.*

@CustomMobOverrides(createFor = [EntityMonster::class, EntityAnimal::class, EntityFlying::class])
abstract class MobBase: EntityInsentient(error(""), error("")), CustomMob {
    //implementation of properties from CustomMob
    override var dead: Boolean
        get() = killed
        set(value) {
            killed = value
        }
    override val nmsEntity: EntityLiving
        get() = this

    override fun lastDamageByPlayerTime(): Int = lastDamageByPlayerTime
    override val killScore: Int = aV

    override fun dropExp() = dropExperience()

    //overriding NMS methods
    override fun initPathfinder() = createPathfinders()

    override fun saveData(nbttagcompound: NMSDataContainer) = super.saveData(nbttagcompound).also { loadMobNBT(nbttagcompound) }
    override fun loadData(nbttagcompound: NMSDataContainer) = super.loadData(nbttagcompound).also { saveMobNBT(nbttagcompound) }

    override fun die(damagesource: DamageSource) = dieCM(damagesource)
    override fun getScoreboardDisplayName() = scoreboardDisplayNameMZ
    override fun getExpValue(entityhuman: EntityHuman): Int = expToDrop()

    override fun getSoundAmbient(): SoundEffect? = null.also { makeSound(soundAmbient) }
    override fun getSoundHurt(damagesource: DamageSource): SoundEffect? = null.also { makeSound(soundHurt) }
    override fun getSoundDeath(): SoundEffect? = null.also { makeSound(soundDeath) }
    override fun a(blockposition: BlockPosition, iblockdata: IBlockData) = makeSound(soundStep)
}