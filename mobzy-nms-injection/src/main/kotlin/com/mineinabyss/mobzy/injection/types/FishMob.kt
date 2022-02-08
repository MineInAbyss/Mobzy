package com.mineinabyss.mobzy.injection.types

import com.mineinabyss.idofront.nms.aliases.*
import com.mineinabyss.idofront.nms.entity.typeName
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import com.mineinabyss.mobzy.injection.CustomEntity
import com.mineinabyss.mobzy.injection.geary
import com.mineinabyss.mobzy.injection.makeSound
import net.minecraft.network.chat.IChatBaseComponent
import net.minecraft.world.entity.EntityTypes
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl
import net.minecraft.world.entity.animal.EntityFishSchool
import net.minecraft.world.level.pathfinder.PathType

//@GenerateFromBase(base = MobBase::class, createFor = [EntityFishSchool::class])
class FishMob(
    type: NMSEntityType<*>, world: NMSWorld
) : EntityFishSchool(type as EntityTypes<out EntityFishSchool>, world), CustomEntity {
    //bucket you get from picking up fish (we disable this interaction anyways)
    override fun getBucketItem(): NMSItemStack =
        NMSItemStack(NMSItems.nX) //Water Bucket TODO let protocolburrito wrap this kinda stuff

    //can't be null so it's harder to make this configurable
    override fun getSoundFlop(): NMSSound = NMSSounds.cY

    //on player interact
    override fun b(entityhuman: NMSEntityHuman, enumhand: NMSHand) = NMSInteractionResult.d //PASS

    init {
        a(PathType.i, 0.0F)
        bM = SmoothSwimmingMoveControl(this, 85, 10, 1.0f, 0.5f, false)
        bL = SmoothSwimmingLookControl(this, 20)
    }

    override fun getScoreboardDisplayName(): IChatBaseComponent = NMSChatMessage(entityType.typeName
        .split('_').joinToString(" ") { it.replaceFirstChar(Char::uppercase) })

    override fun getSoundVolume(): Float = geary.get<Sounds>()?.volume ?: super.getSoundVolume()
    override fun getSoundAmbient(): NMSSound? = makeSound(super.getSoundAmbient()) { ambient }
    override fun getSoundDeath(): NMSSound? = makeSound(super.getSoundDeath()) { death }
    override fun getSoundSplash(): NMSSound? = makeSound(super.getSoundSplash()) { splash }
    override fun getSoundSwim(): NMSSound? = makeSound(super.getSoundSwim()) { swim }
    override fun getSoundHurt(damagesource: NMSDamageSource): NMSSound? =
        makeSound(super.getSoundHurt(damagesource)) { hurt }
}
