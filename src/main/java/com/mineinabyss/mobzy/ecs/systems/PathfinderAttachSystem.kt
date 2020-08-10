package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.mobzy.api.pathfindergoals.addPathfinderGoal
import com.mineinabyss.mobzy.ecs.components.MobzyComponent
import com.mineinabyss.mobzy.mobs.CustomMob
import net.minecraft.server.v1_16_R1.PathfinderGoal
import kotlin.reflect.KClass

object PathfinderAttachSystem: MobzySystem {
    val registeredPathfinders = mutableMapOf<Array<out KClass<out MobzyComponent>>, CustomMob<*>.() -> PathfinderGoal>() //TODO typealias for PathfinderGoal
    
    override fun applyTo(mob: CustomMob<*>) {
        registeredPathfinders.forEach { (classes, goal) ->
            mob.where(*classes) {
                mob.nmsEntity.addPathfinderGoal(1, goal(mob))
            }
        }
    }
    
    fun add(vararg components: KClass<out MobzyComponent>, create: CustomMob<*>.() -> PathfinderGoal){
        registeredPathfinders[components] = create
    }
}