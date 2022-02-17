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
import net.minecraft.world.entity.animal.axolotl.Axolotl
import net.minecraft.world.level.pathfinder.PathType

class HostileWaterMob(
    type: NMSEntityType<*>, world: NMSWorld
) : Axolotl(type as EntityTypes<out Axolotl>, world), CustomEntity {

    override fun getBucketItem(): NMSItemStack =
        NMSItemStack(NMSItems.nX) //Water Bucket

    override fun getScoreboardDisplayName(): IChatBaseComponent = NMSChatMessage(entityType.typeName
        .split('_').joinToString(" ") { it.replaceFirstChar(Char::uppercase) })

    init {
        a(PathType.i, 0.0F)
        bM = SmoothSwimmingMoveControl(this, 85, 10, 0.5f, 0.5f, false)
        bL = SmoothSwimmingLookControl(this, 20)
    }

    override fun getSoundVolume(): Float = geary.get<Sounds>()?.volume ?: super.getSoundVolume()
    override fun getSoundAmbient(): NMSSound? = makeSound(super.getSoundAmbient()) { ambient }
    override fun getSoundDeath(): NMSSound? = makeSound(super.getSoundDeath()) { death }
    override fun getSoundSplash(): NMSSound? = makeSound(super.getSoundSplash()) { splash }
    override fun getSoundSwim(): NMSSound? = makeSound(super.getSoundSwim()) { swim }
    override fun getSoundHurt(damagesource: NMSDamageSource): NMSSound? =
        makeSound(super.getSoundHurt(damagesource)) { hurt }
}