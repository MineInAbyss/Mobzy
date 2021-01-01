package com.mineinabyss.mobzy.ai

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class Action(
    val cost: Int,
) : Comparable<Action> {
    abstract fun conditionsMet(): Boolean
    abstract fun isComplete(): Boolean

    @Transient
    var costInTree = 0

    @Transient
    val preconditions = setOf<Condition>()

    @Transient
    val postconditions = setOf<Condition>()

    @Transient
    val children = sortedSetOf<Action>()

    @Transient
    val parent: Action? = null

    fun canBeChildOf(other: Action) = postconditions.containsAll(other.preconditions)


    abstract fun tick()

    /** Sort by lowest cost first. */
    override fun compareTo(other: Action) = cost.compareTo(other.cost)
}
