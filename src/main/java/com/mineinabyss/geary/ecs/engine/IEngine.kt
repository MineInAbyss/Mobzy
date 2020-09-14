package com.mineinabyss.geary.ecs.engine

import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.geary.ecs.geary
import com.mineinabyss.geary.ecs.systems.TickingSystem
import com.mineinabyss.idofront.plugin.getService
import net.onedaybeard.bitvector.BitVector
import org.bukkit.NamespacedKey

interface Engine {
    companion object : Engine by getService() {
        val componentsKey = NamespacedKey("geary", "components")
    }

    fun getNextId(): Int

    fun addSystem(system: TickingSystem): Boolean

    fun getComponentsFor(id: Int): MutableSet<MobzyComponent>
    fun getComponentFor(kClass: ComponentClass, id: Int): MobzyComponent?
    fun hasComponentFor(kClass: ComponentClass, id: Int): Boolean
    fun removeComponentFor(kClass: ComponentClass, id: Int)
    fun <T : MobzyComponent> addComponentFor(id: Int, component: T): T

    fun removeEntity(id: GearyEntity)

    //TODO this shouldn't be in interface but currently required for inline functions in [Iteration]
    fun getBitsMatching(vararg components: ComponentClass, andNot: Array<out ComponentClass> = emptyArray()): BitVector

    //some helpers
    fun addSystems(vararg systems: TickingSystem) = systems.forEach { addSystem(it) }
    fun addComponentsFor(id: Int, components: Set<MobzyComponent>) = components.forEach { addComponentFor(id, it) }
}

inline fun Engine.entity(run: GearyEntity.() -> Unit): GearyEntity = geary(getNextId(), run)