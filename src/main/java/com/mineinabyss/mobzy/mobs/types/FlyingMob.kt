package com.mineinabyss.mobzy.mobs.types

import com.mineinabyss.idofront.nms.aliases.*
import com.mineinabyss.idofront.nms.entity.typeName
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import com.mineinabyss.mobzy.mobs.CustomEntity
import com.mineinabyss.mobzy.mobs.geary
import com.mineinabyss.mobzy.mobs.makeSound
import net.minecraft.network.chat.IChatBaseComponent
import net.minecraft.world.entity.EntityFlying
import net.minecraft.world.entity.EntityTypes
import net.minecraft.world.entity.ai.control.ControllerMoveFlying

class FlyingMob(
    type: NMSEntityType<*>, world: NMSWorld
) : EntityFlying(type as EntityTypes<out EntityFlying>, world), CustomEntity {
    init {
        //TODO movement controller is being wacko with speed limits
        bM /* ControllerMove */ = ControllerMoveFlying(this, 1, false) /*MZControllerMoveFlying(this)*/
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
