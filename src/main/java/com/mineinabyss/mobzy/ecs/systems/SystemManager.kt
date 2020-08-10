package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.mobzy.mobs.CustomMob

object SystemManager {
    private val registeredSystems: MutableSet<MobzySystem> = mutableSetOf()

    fun runOn(mob: CustomMob<*>) = registeredSystems.forEach { it.applyTo(mob) }

    fun registerSystem(system: MobzySystem) = registeredSystems.add(system)

    fun registerSystems(vararg systems: MobzySystem) = registeredSystems.addAll(systems)
}