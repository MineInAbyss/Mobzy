package com.mineinabyss.looty.ecs.components

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.components.addComponent
import com.mineinabyss.geary.ecs.components.removeComponent
import com.mineinabyss.geary.ecs.remove
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bukkit.inventory.ItemStack

@Serializable
class ChildItemCache(
        @Transient
        private val _itemCache: MutableMap<Int, ItemEntity> = mutableMapOf()
) : GearyComponent(), Map<Int, ItemEntity> by _itemCache {
    operator fun set(index: Int, entity: ItemEntity) {
        _itemCache[index] = entity
    }

    fun clear() {
        _itemCache.values.forEach { it.entity.remove() }
        _itemCache.clear()
    }

    internal fun update(newCache: MutableMap<Int, ItemEntity>) {
        _itemCache.clear()
        _itemCache += newCache
    }

    fun remove(index: Int){
        get(index)?.entity?.remove()
        _itemCache -= index
    }

    fun swapHeldComponent(removeFrom: Int, addTo: Int) {
        this[removeFrom]?.entity?.removeComponent<Held>()
        this[addTo]?.entity?.addComponent(Held())
    }

    fun swapSlotCache(first: Int, second: Int) {
        val firstCache = _itemCache[first]
        val secondCache = _itemCache[second]

        if (firstCache == null)
            _itemCache.remove(second)
        else
            _itemCache[second] = firstCache

        if (secondCache == null)
            _itemCache.remove(first)
        else
            _itemCache[first] = secondCache
    }
}

data class ItemEntity(val item: ItemStack, val entity: GearyEntity)