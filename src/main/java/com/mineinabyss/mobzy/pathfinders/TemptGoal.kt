package com.mineinabyss.mobzy.pathfinders

import com.mineinabyss.mobzy.api.helpers.entity.distanceSqrTo
import com.mineinabyss.mobzy.api.helpers.entity.findNearbyPlayer
import com.mineinabyss.mobzy.api.helpers.entity.lookAt
import com.mineinabyss.mobzy.api.nms.aliases.living
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.mobs.AnyCustomMob
import com.mineinabyss.mobzy.mobs.CustomMob
import net.minecraft.server.v1_16_R1.EntityLiving
import org.bukkit.Material
import org.bukkit.event.entity.EntityTargetEvent

//TODO javadoc
class TemptGoal(override val mob: AnyCustomMob, targetItems: List<Material>?, private val speed: Double = 1.0) : MobzyPathfinderGoal(cooldown = 100) {
    private val targetItems = targetItems ?: error("Cannot create pathfinder without tempt items")

    override fun shouldExecute(): Boolean {
        //TODO don't find a new player every time, just check if existing target is within range, add range as param in constructor
        val nearbyPlayer = mob.entity.findNearbyPlayer(10.0)?.living ?: return false
        val equipment = nearbyPlayer.equipment ?: return false

        if (targetItems.any { it == equipment.itemInMainHand.type || it == equipment.itemInOffHand.type }) {
            //run the event
            //TODO make a neat way to do this through MobzyPathfinderGoal
            if (target == nearbyPlayer) return true
            return nmsEntity.setGoalTarget(nearbyPlayer.toNMS<EntityLiving>(), EntityTargetEvent.TargetReason.TEMPT, true)
        }
        return false
    }

    override fun shouldKeepExecuting(): Boolean = shouldExecute()

    override fun execute() {
        val target = target ?: return
        entity.lookAt(target)
    }

    override fun executeWhenCooledDown() {
        val target = target ?: return
        restartCooldown()
        val dist = mob.entity.distanceSqrTo(target)
        if (dist in 1.0..36.00) navigation.moveToEntity(target, speed)
    }
}