package com.offz.spigot.abyssalcreatures.mobs.hostile

import com.offz.spigot.abyssalcreatures.mobs.passive.Neritantan
import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour
import com.offz.spigot.mobzy.mobs.types.HostileMob
import net.minecraft.server.v1_15_R1.PathfinderGoalNearestAttackableTarget
import net.minecraft.server.v1_15_R1.World

class Inbyo(world: World?) : HostileMob(world, "Inbyo"), HitBehaviour {
    override fun createPathfinders() {
        super.createPathfinders()
        addTargetSelector(1, PathfinderGoalNearestAttackableTarget(this, Neritantan::class.java, true))
    }
}