package com.offz.spigot.abyssalcreatures.mobs.passive

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour
import com.offz.spigot.mobzy.mobs.types.PassiveMob
import net.minecraft.server.v1_15_R1.EntityHuman
import net.minecraft.server.v1_15_R1.World

class Okibo(world: World?) : PassiveMob(world, "Okibo"), HitBehaviour {
    override fun onRightClick(player: EntityHuman) {
        player.startRiding(this)
    }
}