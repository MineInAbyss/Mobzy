package com.mineinabyss.mobzy.mobs.types

import com.mineinabyss.idofront.nms.aliases.*
import com.mineinabyss.idofront.nms.entity.typeName
import com.mineinabyss.mobzy.api.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.api.pathfindergoals.addTargetSelector
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import com.mineinabyss.mobzy.ecs.goals.minecraft.*
import com.mineinabyss.mobzy.ecs.goals.targetselectors.minecraft.TargetNearbyPlayer
import com.mineinabyss.mobzy.mobs.geary
import com.mineinabyss.mobzy.mobs.makeSound
import net.minecraft.network.chat.IChatBaseComponent
import net.minecraft.world.entity.EntityTypes
import net.minecraft.world.entity.monster.EntityMonster

open class HostileMob(
    type: NMSEntityType<*>, world: NMSWorld
) : EntityMonster(type as EntityTypes<out EntityMonster>, world) {
    override fun initPathfinder() {
        addPathfinderGoal(2, MeleeAttackBehavior(attackSpeed = 1.0, seeThroughWalls = false))
        addPathfinderGoal(3, FloatBehavior())
        addPathfinderGoal(7, RandomStrollLandBehavior())
        addPathfinderGoal(7, LookAtPlayerBehavior(radius = 8.0f))
        addPathfinderGoal(8, RandomLookAroundBehavior())

        addTargetSelector(2, TargetNearbyPlayer())
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
