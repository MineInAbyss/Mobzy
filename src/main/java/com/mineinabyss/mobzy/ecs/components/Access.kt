package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.geary.ecs.Engine
import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.mobzy.api.toMobzy
import com.mineinabyss.mobzy.mobs.CustomMob
import org.bukkit.entity.Mob

fun CustomMob.addComponent(component: MobzyComponent) = Engine.addComponent(mobzyId, component)
fun CustomMob.addComponents(components: Set<MobzyComponent>) = Engine.addComponentsFor(mobzyId, components)

inline fun <reified T : MobzyComponent> CustomMob.get(): T? = Engine.get(mobzyId)

inline fun <reified T : MobzyComponent> CustomMob.has() = Engine.has<T>(mobzyId)

fun Mob.addComponent(component: MobzyComponent) = (this as? CustomMob)?.let {
    Engine.addComponent(mobzyId, component)
}

//TODO a way of getting ID given a vanilla entity as fallback
inline fun <reified T : MobzyComponent> Mob.get(): T? = toMobzy()?.let { Engine.get(it.mobzyId) }

inline fun <reified T : MobzyComponent> Mob.has() = toMobzy()?.let { Engine.has<T>(it.mobzyId) } ?: false