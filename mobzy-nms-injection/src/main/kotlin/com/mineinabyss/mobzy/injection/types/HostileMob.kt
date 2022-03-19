package com.mineinabyss.mobzy.injection.types

import com.mineinabyss.geary.papermc.GearyMCContext
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.NMSWorld
import com.mineinabyss.mobzy.ecs.components.ambient.Sounds
import com.mineinabyss.mobzy.injection.CustomEntity
import com.mineinabyss.mobzy.injection.makeSound
import com.mineinabyss.mobzy.injection.toGeary
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.monster.Monster

context(GearyMCContext)
open class HostileMob(
    type: NMSEntityType<out Monster>, world: NMSWorld
) : Monster(type, world), CustomEntity {
    //TODO do not register any pathfinders automatically

//    override fun registerGoals() {
//        addPathfinderGoal(2, MeleeAttackBehavior(attackSpeed = 1.0, seeThroughWalls = false))
//        addPathfinderGoal(3, FloatBehavior())
//        addPathfinderGoal(7, RandomStrollLandBehavior())
//        addPathfinderGoal(7, LookAtPlayerBehavior(radius = 8.0f))
//        addPathfinderGoal(8, RandomLookAroundBehavior())
//
//        addTargetSelector(2, TargetAttacker())
//        addTargetSelector(6, TargetNearbyPlayerCustom())
//    }

    override fun getTypeName(): Component =
        TextComponent(type.descriptionId.split('_').joinToString(" ") { it.replaceFirstChar(Char::uppercase) })

    override fun getSoundVolume(): Float = toGeary().get<Sounds>()?.volume ?: super.getSoundVolume()
    override fun getAmbientSound(): SoundEvent? = makeSound(super.getAmbientSound()) { ambient }
    override fun getDeathSound(): SoundEvent? = makeSound(super.getDeathSound()) { death }
    override fun getSwimSound(): SoundEvent? = makeSound(super.getSwimSplashSound()) { swim }
    override fun getSwimSplashSound(): SoundEvent? = makeSound(super.getSwimSplashSound()) { splash }
    override fun getHurtSound(source: DamageSource): SoundEvent? = makeSound(super.getHurtSound(source)) { hurt }
}
