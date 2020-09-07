package com.mineinabyss.geary.ecs.components

import com.mineinabyss.geary.ecs.Engine
import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.mobzy.api.toMobzy
import com.mineinabyss.mobzy.mobs.CustomMob
import org.bukkit.entity.Mob

fun GearyEntity.addComponent(component: MobzyComponent) = Engine.addComponent(gearyId, component)
fun GearyEntity.addComponents(components: Set<MobzyComponent>) = Engine.addComponentsFor(gearyId, components)
inline fun <reified T : MobzyComponent> GearyEntity.getOrAdd(component: () -> T) = Engine.getOrDefault(gearyId, component)

inline fun <reified T : MobzyComponent> GearyEntity.get(): T? = Engine.get(gearyId)

inline fun <reified T : MobzyComponent> GearyEntity.has() = Engine.has<T>(gearyId)

fun Mob.addComponent(component: MobzyComponent) = (this as? CustomMob)?.let {
    Engine.addComponent(gearyId, component)
}

//TODO a way of getting ID given a vanilla entity as fallback
inline fun <reified T : MobzyComponent> Mob.get(): T? = toMobzy()?.let { Engine.get(it.gearyId) }

inline fun <reified T : MobzyComponent> Mob.has() = toMobzy()?.let { Engine.has<T>(it.gearyId) } ?: false