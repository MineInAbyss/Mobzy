package com.mineinabyss.mobzy.injection.types

import com.mineinabyss.idofront.messaging.logInfo
import com.mineinabyss.idofront.nms.aliases.*
import com.mineinabyss.idofront.nms.entity.typeName
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import com.mineinabyss.mobzy.ecs.goals.minecraft.*
import com.mineinabyss.mobzy.ecs.goals.targetselectors.TargetAttacker
import com.mineinabyss.mobzy.ecs.goals.targetselectors.TargetNearbyPlayerCustom
import com.mineinabyss.mobzy.injection.CustomEntity
import com.mineinabyss.mobzy.injection.geary
import com.mineinabyss.mobzy.injection.makeSound
import com.mineinabyss.mobzy.pathfinding.addPathfinderGoal
import com.mineinabyss.mobzy.pathfinding.addTargetSelector
import net.minecraft.network.chat.IChatBaseComponent
import net.minecraft.world.entity.EntityTypes
import net.minecraft.world.entity.monster.EntityMonster

open class HostileMob(
    type: NMSEntityType<*>, world: NMSWorld
) : EntityMonster(type as EntityTypes<out EntityMonster>, world), CustomEntity {
    //TODO do not register any pathfinders automatically
    override fun initPathfinder() {
        addPathfinderGoal(2, MeleeAttackBehavior(attackSpeed = 1.0, seeThroughWalls = false))
        addPathfinderGoal(3, FloatBehavior())
        addPathfinderGoal(7, RandomStrollLandBehavior())
        addPathfinderGoal(7, LookAtPlayerBehavior(radius = 8.0f))
        addPathfinderGoal(8, RandomLookAroundBehavior())
        logInfo(world.time)

        addTargetSelector(6, TargetNearbyPlayerCustom())
        logInfo("TargetNearbyPlayer registered")
        addTargetSelector(2, TargetAttacker())
        logInfo("TargetAttacker registered")
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
