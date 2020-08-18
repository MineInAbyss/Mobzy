package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.geary.ecs.Engine
import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.mobzy.mobs.CustomMob

fun CustomMob.addComponent(component: MobzyComponent) = Engine.addComponent(mobzyId, component)

inline fun <reified T : MobzyComponent> CustomMob.get(): T? = Engine.get(mobzyId)

inline fun <reified T : MobzyComponent> CustomMob.has() = Engine.has<T>(mobzyId)