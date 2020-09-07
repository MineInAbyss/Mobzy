package com.mineinabyss.mobzy.ai

import com.mineinabyss.geary.ecs.Engine
import com.mineinabyss.geary.ecs.MobzyComponent
import com.mineinabyss.geary.ecs.systems.TickingSystem
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Goals(
        val actions: Set<Action>,
        val goals: Set<Action>,
) : MobzyComponent() {
    @Transient
    var executingPlan: Action? = null

    tailrec fun runPlan() {
        val plan = executingPlan ?: return

        //re-evaluate plan if conditions are no longer met
        if (!plan.conditionsMet()) {
            executingPlan = null
            return
        }

        //move up to parent if plan is complete
        if (plan.isComplete()) {
            executingPlan = plan.parent
            runPlan()
        } else plan.tick()
    }
}

class GoalManagerSystem : TickingSystem() {
    override fun tick() = Engine.runFor<Goals> { goalComponent ->
        goalComponent.executingPlan?.let { plan ->
            plan.tick()
            return@runFor
        }
        val (actions, goals) = goalComponent
        goals.forEach { goal ->
            val openNodes = sortedSetOf(goal)
            val closedNodes = sortedSetOf<Action>()

            while (openNodes.isNotEmpty()) {
                val action = openNodes.first()
                openNodes.remove(action)
                closedNodes.add(action)
                if (action.conditionsMet()) {
                    goalComponent.executingPlan = action
                    return@forEach
                }

                actions.childrenOf(action)
                        .filter { !closedNodes.contains(it) }
                        .forEach {
                            it.costInTree = action.costInTree + it.cost
                            openNodes.add(it)
                        }
            }
        }
    }
}

//TODO cache children inside action
fun Collection<Action>.childrenOf(action: Action) = filter { it.canBeChildOf(action) }