package com.offz.spigot.mobzy.pathfinders

import net.minecraft.server.v1_15_R1.PathfinderGoal

abstract class PathfinderGoal: PathfinderGoal() {
    override fun a() = shouldExecute()

    abstract fun shouldExecute(): Boolean

    override fun b() = shouldTerminate()

    abstract fun shouldTerminate(): Boolean

    override fun c() = init()

    abstract fun init()

    override fun d() = reset()

    abstract fun reset()

    override fun e() = execute()

    abstract fun execute()

}