package com.offz.spigot.mobzy.pathfinders

import net.minecraft.server.v1_15_R1.PathfinderGoal

/**
 * Original methods by Yannick Lamprecht under the MIT license from https://github.com/yannicklamprecht/PathfindergoalAPI
 */

abstract class PathfinderGoal : PathfinderGoal() {

    /**
     * Whether the pathfinder goal should commence execution or not
     *
     * @return true if should execute
     */
    abstract fun shouldExecute(): Boolean

    override fun a() = shouldExecute()

    /**
     * Whether the goal should Terminate
     *
     * @return true if should terminate
     */
    abstract fun shouldTerminate(): Boolean

    override fun b() = shouldTerminate()

    /**
     * Runs initially and should be used to setUp goalEnvironment Condition needs to be defined thus
     * your code in it isn't called
     */
    abstract fun init()

    override fun c() = init()

    /**
     * Reset the pathfinder AI pack to its initial state
     *
     * Is called when [shouldExecute] returns false
     */
    abstract fun reset()

    override fun d() = reset()

    /**
     * Is called when [shouldExecute] returns true
     */
    abstract fun execute()

    override fun e() = execute()

}