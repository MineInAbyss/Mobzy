package com.mineinabyss.mobzy.spawning

import java.util.*

class RandomCollection<E> @JvmOverloads constructor(private val random: Random = Random()) {
    private val map: NavigableMap<Double, E> = TreeMap()
    private var total = 0.0

    fun add(weight: Double, result: E): RandomCollection<E> {
        if (weight <= 0) return this
        total += weight
        map[total] = result
        return this
    }

    operator fun next(): E {
        val value = random.nextDouble() * total
        return map.higherEntry(value).value
    }

    val isEmpty: Boolean get() = map.isEmpty()
}