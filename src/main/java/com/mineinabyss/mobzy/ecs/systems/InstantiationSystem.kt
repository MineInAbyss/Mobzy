package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.mobzy.ecs.components.MobzyComponent
import com.mineinabyss.mobzy.mobs.CustomMob
import kotlin.reflect.KClass

abstract class TickingSystem(val interval: Int = 1) {
    abstract fun tick()
}