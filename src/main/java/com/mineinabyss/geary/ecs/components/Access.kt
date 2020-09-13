package com.mineinabyss.geary.ecs.components

import com.mineinabyss.geary.ecs.Engine
import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.mobzy.mobs.CustomMob
import org.bukkit.entity.Mob

fun GearyEntity.addComponent(component: MobzyComponent) = Engine.addComponent(gearyId, component)
fun GearyEntity.addComponents(components: Set<MobzyComponent>) = Engine.addComponentsFor(gearyId, components)
inline fun <reified T : MobzyComponent> GearyEntity.removeComponent() =
        Engine.removeComponentFor<T>(gearyId)

inline fun <reified T : MobzyComponent> GearyEntity.getOrAdd(component: () -> T) = Engine.getOrDefault(gearyId, component)

inline fun <reified T : MobzyComponent> GearyEntity.get(): T? = Engine.getFor(gearyId)

inline fun <reified T : MobzyComponent> GearyEntity.with(let: (T) -> Unit) = get<T>()?.let(let)

inline fun <reified T : MobzyComponent> GearyEntity.has() = Engine.has<T>(gearyId)

fun Mob.addComponent(component: MobzyComponent) = (this as? CustomMob)?.let {
    Engine.addComponent(gearyId, component)
}