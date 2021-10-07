package com.mineinabyss.mobzy.mobs.types

import com.mineinabyss.idofront.nms.aliases.*
import com.mineinabyss.idofront.nms.entity.typeName
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import com.mineinabyss.mobzy.mobs.CustomEntity
import com.mineinabyss.mobzy.mobs.geary
import com.mineinabyss.mobzy.mobs.makeSound
import net.minecraft.network.chat.IChatBaseComponent
import net.minecraft.server.level.WorldServer
import net.minecraft.world.entity.EntityAgeable
import net.minecraft.world.entity.EntityTypes
import net.minecraft.world.entity.animal.EntityAnimal

open class PassiveMob(
    type: NMSEntityType<*>, world: NMSWorld
) : EntityAnimal(type as EntityTypes<out EntityAnimal>, world), CustomEntity {

    override fun createChild(worldServer: WorldServer, entityAgeable: EntityAgeable): EntityAgeable? = null

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
