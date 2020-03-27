package com.mineinabyss.mobzy.pathfinders.flying

import com.mineinabyss.mobzy.mobs.types.FlyingMob
import com.mineinabyss.mobzy.pathfinders.MobzyPathfinderGoal
import kotlin.math.abs
import kotlin.random.Random

class PathfinderGoalDiveOnTargetAttack(
        override val mob: FlyingMob,
        private val diveVelocity: Double = -0.3,
        private val minHeight: Double = 6.0,
        private val maxHeight: Double = 10.0,
        private val startDiveDistance: Double = 16.0,
        private val startDiveHeightRange: Double = 2.0,
        private val bashVelMultiplier: Double = 0.6,
        private val bashDuration: Double = 30.0) : MobzyPathfinderGoal() {
    private var currentAction = Action.FLY
    private var diveHeight: Double = pickDiveHeight()
    private var bashLeft = bashDuration
    private var bashVelX = 0.0
    private var bashVelZ = 0.0

    override fun shouldExecute() = target != null

    override fun execute() = when (currentAction) {
        Action.FLY -> prepareDive()
        Action.DIVE -> beginDive()
        Action.BASH -> bash()
    }

    override fun shouldKeepExecuting() = target != null

    override fun reset() {
        bashLeft = bashDuration
        currentAction = Action.FLY
        moveController.a() //this should reset the controller's destination FIXME this method doesn't actually do that
    }

    private fun prepareDive() {
        val target = target ?: return
        mob.lookAtPitchLock(target)

        //if arrived to dive
        val diveTarget = target.location.clone().add(0.0, diveHeight, 0.0)
        if (diveTarget.distance(target.location) < startDiveDistance && abs(mob.locY - diveTarget.y) < startDiveHeightRange) {
            diveHeight = pickDiveHeight()
            currentAction = Action.DIVE
            return
        }

        val targetLoc = target.location
        moveController.a(targetLoc.x, targetLoc.y + diveHeight, targetLoc.z, 1.0) //TODO use controllerMove wrapper
    }

    private fun beginDive() {
        val target = target ?: return
        mob.lookAtPitchLock(target)
        val targetLoc = target.location
        if (mob.distanceTo(target) < 2 || entity.velocity.y == 0.0 || mob.locY <= target.location.y + 1.0) {
            currentAction = Action.BASH
            bashVelX = entity.location.direction.x * bashVelMultiplier
            bashVelZ = entity.location.direction.z * bashVelMultiplier
            return
        }
        moveController.a(targetLoc.x, targetLoc.y, targetLoc.z, 1.0)
        entity.velocity = entity.velocity.setY(-abs(diveVelocity))
    }

    private fun bash() {
        mob.lookAt(mob.locX + bashVelX, mob.locZ + bashVelZ)
        entity.velocity = entity.velocity.setX(bashVelX).setZ(bashVelZ)
        if (bashLeft-- <= 0 || target == null || mob.distanceTo(target!!) < 2 || entity.velocity.x == 0.0 || entity.velocity.z == 0.0) {
            currentAction = Action.FLY
            bashLeft = bashDuration
        }
    }

    private fun pickDiveHeight() = Random.nextDouble(minHeight, maxHeight)

    private enum class Action {
        FLY, DIVE, BASH
    }
}