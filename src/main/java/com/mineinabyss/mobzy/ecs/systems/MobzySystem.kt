package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.mobzy.ecs.components.MobzyComponent
import com.mineinabyss.mobzy.mobs.CustomMob
import kotlin.reflect.KClass

interface MobzySystem {
    fun applyTo(mob: CustomMob<*>)
}

fun CustomMob<*>.where(vararg components: KClass<out MobzyComponent>, block: () -> Unit){
    val typeComponenets = type.components
    if(components.all { typeComponenets.contains(it) })
        block()
}