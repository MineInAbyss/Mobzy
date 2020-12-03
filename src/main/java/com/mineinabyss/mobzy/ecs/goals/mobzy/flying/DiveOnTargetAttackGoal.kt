package com.mineinabyss.mobzy.ecs.goals.mobzy.flying

import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.idofront.destructure.component2
import com.mineinabyss.idofront.destructure.component3
import com.mineinabyss.mobzy.api.helpers.entity.distanceSqrTo
import com.mineinabyss.mobzy.api.helpers.entity.lookAt
import com.mineinabyss.mobzy.api.helpers.entity.lookAtPitchLock
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Mob
import kotlin.math.abs
import kotlin.math.pow
import kotlin.random.Random

@Serializable
@SerialName("mobzy:behavior.flying_dive_on_target")
class DiveOnTargetBehavior(
        private val diveVelocity: Double = -0.3,
        private val minHeight: Double = 6.0,
        private val maxHeight: Double = 10.0,
        private val startDiveDistance: Double = 16.0,
        private val startDiveHeightRange: Double = 2.0,
        private val bashVelMultiplier: Double = 0.6,
        private val bashDuration: Double = 30.0
) : PathfinderComponent() {
    override fun build(mob: Mob) = DiveOnTargetAttackGoal(
            mob,
            diveVelocity,
            minHeight,
            maxHeight,
            startDiveDistance,
            startDiveHeightRange,
            bashVelMultiplier,
            bashDuration
    )
}

class DiveOnTargetAttackGoal(
        override val mob: Mob,
        private val diveVelocity: Double = -0.3,
        private val minHeight: Double = 6.0,
        private val maxHeight: Double = 10.0,
        private val startDiveDistance: Double = 16.0,
        private val startDiveHeightRange: Double = 2.0,
        private val bashVelMultiplier: Double = 0.6,
        private val bashDuration: Double = 30.0
) : MobzyPathfinderGoal(type = Type.MOVE) {
    private var currentAction = Action.FLY
    private var diveHeight: Double = pickDiveHeight()
    private var bashLeft = bashDuration
    private var bashVelX = 0.0
    private var bashVelZ = 0.0

    override fun shouldExecute() = mob.target != null

    override fun execute() = when (currentAction) {
        Action.FLY -> prepareDive()
        Action.DIVE -> beginDive()
        Action.BASH -> bash()
    }

    override fun shouldKeepExecuting() = mob.target != null

    override fun reset() {
        bashLeft = bashDuration
        currentAction = Action.FLY
        moveController.a() //this should reset the controller's destination FIXME this method doesn't actually do that
    }

    private fun prepareDive() {
        val target = mob.target ?: return
        mob.lookAtPitchLock(target)

        //if arrived to dive
        //TODO dont make so many location instances
        val diveTarget = target.location.add(0.0, diveHeight, 0.0)
        if (mob.distanceSqrTo(diveTarget) < startDiveDistance.pow(2) && abs(mob.location.y - diveTarget.y) < startDiveHeightRange) {
            diveHeight = pickDiveHeight()
            currentAction = Action.DIVE
            return
        }

        val targetLoc = target.location
        moveController.a(targetLoc.x, targetLoc.y + diveHeight, targetLoc.z, 1.0) //TODO use controllerMove wrapper
    }

    private fun beginDive() {
        val target = mob.target ?: return
        mob.lookAtPitchLock(target)
        val targetLoc = target.location
        if (mob.distanceSqrTo(target) < 2 || mob.velocity.y == 0.0 || mob.location.y <= target.location.y + 1.0) {
            currentAction = Action.BASH
            bashVelX = mob.location.direction.x * bashVelMultiplier
            bashVelZ = mob.location.direction.z * bashVelMultiplier
            return
        }
        moveController.a(targetLoc.x, targetLoc.y, targetLoc.z, 1.0)
        mob.velocity = mob.velocity.setY(-abs(diveVelocity))
    }

    private fun bash() {
        val (x, _, z) = mob.location
        mob.lookAt(x + bashVelX, z + bashVelZ)
        mob.velocity = mob.velocity.setX(bashVelX).setZ(bashVelZ)
        if (bashLeft-- <= 0 || mob.target == null || mob.distanceSqrTo(mob.target!!) < 2 || mob.velocity.x == 0.0 || mob.velocity.z == 0.0) {
            currentAction = Action.FLY
            bashLeft = bashDuration
        }
    }

    private fun pickDiveHeight() = Random.nextDouble(minHeight, maxHeight)

    private enum class Action {
        FLY, DIVE, BASH
    }
}