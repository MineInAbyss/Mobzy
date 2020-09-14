@file:Suppress("NOTHING_TO_INLINE")

package com.mineinabyss.geary.ecs.components

import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.geary.ecs.engine.Engine

inline fun <T : MobzyComponent> GearyEntity.addComponent(component: T): T = Engine.addComponentFor(gearyId, component)
inline fun GearyEntity.addComponents(components: Set<MobzyComponent>) = Engine.addComponentsFor(gearyId, components)

inline fun <reified T : MobzyComponent> GearyEntity.removeComponent() =
        Engine.removeComponentFor(T::class, gearyId)

inline fun <reified T : MobzyComponent> GearyEntity.getOrAdd(component: () -> T) = get<T>() ?: addComponent(component())

inline fun <reified T : MobzyComponent> GearyEntity.get(): T? = Engine.getComponentFor(T::class, gearyId) as? T

inline fun <reified T : MobzyComponent> GearyEntity.with(let: (T) -> Unit) = get<T>()?.let(let)

inline fun <reified T : MobzyComponent> GearyEntity.has() = Engine.hasComponentFor(T::class, gearyId)

//TODO remove
//fun Mob.addComponent(component: MobzyComponent) = (this as? CustomMob)?.let {
//    Engine.addComponentFor(gearyId, component)
//}