package com.mineinabyss.geary.ecs.engine

import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.geary.ecs.geary


//TODO support component families with infix functions
inline fun Engine.forEach(vararg components: ComponentClass, andNot: Array<out ComponentClass> = emptyArray(), run: GearyEntity.(List<MobzyComponent>) -> Unit) {
    Engine.getBitsMatching(*components, andNot = andNot)?.forEachBit { index ->
        geary(index).run(components.map { getComponentFor(it, index) ?: return@forEachBit })
    }
}

//TODO clean up and expand for more parameters
inline fun <reified T : MobzyComponent> Engine.forEach(vararg andNot: ComponentClass = emptyArray(), run: GearyEntity.(T) -> Unit) {
    val tClass = T::class
    getBitsMatching(tClass, andNot = andNot)?.forEachBit { index ->
        geary(index).run(getComponentFor(tClass, index) as T)
    }
}

inline fun <reified T : MobzyComponent, reified T2 : MobzyComponent> Engine.forEach(vararg andNot: ComponentClass = emptyArray(), run: GearyEntity.(T, T2) -> Unit) {
    val tClass = T::class
    val t2Class = T2::class
    getBitsMatching(tClass, t2Class, andNot = andNot)?.forEachBit { index ->
        geary(index).run(getComponentFor(tClass, index) as T, getComponentFor(t2Class, index) as T2)
    }
}