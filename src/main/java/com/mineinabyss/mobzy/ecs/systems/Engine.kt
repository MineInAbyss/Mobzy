package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.mobzy.ecs.components.MobzyComponent
import com.mineinabyss.mobzy.ecs.systems.Engine.bitsets
import com.mineinabyss.mobzy.ecs.systems.Engine.components
import com.mineinabyss.mobzy.mobs.AnyCustomMob
import com.mineinabyss.mobzy.mobzy
import net.onedaybeard.bitvector.BitVector
import net.onedaybeard.bitvector.bitsOf
import org.bukkit.Bukkit
import org.clapper.util.misc.SparseArrayList
import kotlin.reflect.KClass

object Engine {
    init {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(mobzy, {
            registeredSystems.filter { it.interval == 1 || Bukkit.getCurrentTick() % it.interval == 0 }.forEach(TickingSystem::tick)
        }, 1, 1)
    }

    //TODO use archetypes instead
    val registeredSystems = mutableSetOf<TickingSystem>()

    val components = mutableMapOf<KClass<out MobzyComponent>, SparseArrayList<MobzyComponent>>()
    val bitsets = mutableMapOf<KClass<out MobzyComponent>, BitVector>()

    inline fun runFor(vararg components: KClass<out MobzyComponent>, run: (List<MobzyComponent>) -> Unit) {
        getBitsMatching(*components)?.forEachBit { index ->
            components.map { getComponentForId(it, index) ?: return@forEachBit }.apply(run)
        }
    }

    inline fun <reified T:MobzyComponent> runFor(run: (T) -> Unit){
        val tClass = T::class
        getBitsMatching(tClass)?.forEachBit { index ->
            (getComponentForId(tClass, index) as? T)?.apply(run)
        }
    }

    inline fun <reified T:MobzyComponent, reified T2: MobzyComponent> runFor(run: (T, T2) -> Unit){
        val tClass = T::class
        val t2Class = T2::class
        getBitsMatching(tClass, t2Class)?.forEachBit { index ->
            run(getComponentForId(tClass, index) as T, getComponentForId(t2Class, index) as T2)
        }
    }

    fun getBitsMatching(vararg components: KClass<out MobzyComponent>): BitVector? {
        return components.map { (bitsets[it] ?: return null).copy() }
                .reduce { a, b -> a.and(b).let { a } }
    }

    fun getComponentForId(component: KClass<out MobzyComponent>, id: Int): MobzyComponent? {
        return components[component]?.get(id)
    }

    inline fun <reified T : MobzyComponent> get(id: Int): T? = components[T::class]?.get(id) as? T //TODO unsure if this is null-safe

    inline fun <reified T : MobzyComponent> has(id: Int) = bitsets[T::class]?.contains(id) ?: false


    private var currId = 0

    fun getNextId() = ++currId

    fun addEntity() = getNextId()
}

fun AnyCustomMob.addComponent(component: MobzyComponent) {
    components.getOrPut(component::class, { SparseArrayList() })[mobzyId] = component
    bitsets.getOrPut(component::class, { bitsOf() }).set(mobzyId)
}
