package com.mineinabyss.mobzy.ai

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.forEach
import com.mineinabyss.geary.ecs.systems.TickingSystem
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Goals(
        val actions: Set<Action>,
        val goals: Set<Action>,
) : GearyComponent() {
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
    override fun tick() = Engine.forEach<Goals> { goalComponent ->
        goalComponent.executingPlan?.let { plan ->
            plan.tick()
            return@forEach
        }
        val (actions, goals) = goalComponent
        for (goal in goals) {
            val openNodes = sortedSetOf(goal)
            val closedNodes = sortedSetOf<Action>()

            while (openNodes.isNotEmpty()) {
                val action = openNodes.first()
                openNodes.remove(action)
                closedNodes.add(action)
                if (action.conditionsMet()) {
                    goalComponent.executingPlan = action
                    continue
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