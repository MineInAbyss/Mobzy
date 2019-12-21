package com.offz.spigot.mobzy.pathfinders

import com.offz.spigot.mobzy.mobs.CustomMob
import com.offz.spigot.mobzy.toNMS
import net.minecraft.server.v1_15_R1.EntityLiving
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_15_R1.event.CraftEventFactory
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityTargetEvent

class PathfinderGoalTemptPitchLock(private val mob: CustomMob, private val targetItems: List<Material>) : com.offz.spigot.mobzy.pathfinders.PathfinderGoal() {
    private var target: LivingEntity? = null
    private val navigation = mob.navigation
    private val entity = mob.entity
    private var i: Int = 0

    override fun shouldExecute(): Boolean {
        /*return if (i > 0) {
            --i
            false
        } else {*/
        target = mob.findNearbyPlayer(10.0)?.living
        if (target == null) return false

        val equipment = target!!.equipment ?: return false

        return if (targetItems.any { it == equipment.itemInMainHand.type || it == equipment.itemInOffHand.type }) {
            //run the event
            val event = CraftEventFactory.callEntityTargetLivingEvent(entity, target?.toNMS() as EntityLiving, EntityTargetEvent.TargetReason.TEMPT)
            if (event.isCancelled) return false
            true
        } else false
    }

    override fun shouldTerminate(): Boolean = false

    override fun init() {
    }

    override fun reset() {
    }

    override fun execute() {
        mob.lookAtPitchLock(target!!)
        if (mob.distanceToEntity(target!!) < 6.25) navigation.moveToEntity(target!!)
    }
}