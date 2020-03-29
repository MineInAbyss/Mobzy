package com.mineinabyss.mobzy.pathfinders

import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.toNMS
import net.minecraft.server.v1_15_R1.EntityLiving
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_15_R1.event.CraftEventFactory
import org.bukkit.event.entity.EntityTargetEvent

class PathfinderGoalTemptPitchLock(override val mob: CustomMob, targetItems: List<Material>?, private val speed: Double = 1.0) : MobzyPathfinderGoal(cooldown = 200) {
    private val targetItems = targetItems ?: error("Cannot create pathfinder without tempt items")

    override fun shouldExecute(): Boolean {
        val nearbyPlayer = mob.findNearbyPlayer(10.0)?.living ?: return false
        val equipment = nearbyPlayer.equipment ?: return false

        if (targetItems.any { it == equipment.itemInMainHand.type || it == equipment.itemInOffHand.type }) {
            //run the event
            val event = CraftEventFactory.callEntityTargetLivingEvent(nmsEntity, nearbyPlayer.toNMS() as EntityLiving, EntityTargetEvent.TargetReason.TEMPT)
            if (event.isCancelled) return false
            target = nearbyPlayer
            return true
        }
        return false
    }

    override fun shouldKeepExecuting(): Boolean = shouldExecute()

    override fun execute() {
        val target = target ?: return
        mob.lookAtPitchLock(target)
    }

    override fun executeWhenCooledDown() {
        val target = target ?: return
        restartCooldown()
        val dist = mob.distanceTo(target)
        if (dist in 1.0..6.25) navigation.moveToEntity(target, speed)
    }
}