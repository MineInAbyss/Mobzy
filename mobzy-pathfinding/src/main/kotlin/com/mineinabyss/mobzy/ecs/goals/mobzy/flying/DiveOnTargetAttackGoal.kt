package com.mineinabyss.mobzy.ecs.goals.mobzy.flying

import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.idofront.destructure.component2
import com.mineinabyss.idofront.destructure.component3
import com.mineinabyss.idofront.location.up
import com.mineinabyss.mobzy.ecs.components.initialization.pathfinding.PathfinderComponent
import com.mineinabyss.mobzy.modelengine.playAnimation
import com.mineinabyss.mobzy.modelengine.stopAnimation
import com.mineinabyss.mobzy.pathfinding.MobzyPathfinderGoal
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
) : MobzyPathfinderGoal(cooldown = 20L, flags = listOf(Flag.MOVE)) {
    private var currentAction = Action.FLY
    private var diveHeight: Double = pickDiveHeight()
    private var bashLeft = bashDuration
    private var bashVelX = 0.0
    private var bashVelZ = 0.0
//    private var offsetTarget = Vector()

    override fun shouldExecute() = mob.target != null

    override fun shouldKeepExecuting(): Boolean = shouldExecute()

    override fun execute() {
        val move = nmsEntity.moveControl
        val dist = nmsEntity.distanceToSqr(move.wantedX, move.wantedY, move.wantedZ)
        val target = mob.target ?: return

        // If too far or no target
        if (!move.hasWanted() || dist > 3600.0) {
            prepareDive()
            return
        }


        when (currentAction) {
            Action.FLY -> {
                val diveTarget = target.location.up(diveHeight)
                if (mob.location.distanceSquared(diveTarget) < startDiveDistance.pow(2)
                    && abs(mob.location.y - diveTarget.y) < startDiveHeightRange
                ) beginDive()
            }
            Action.DIVE -> {
                if (mob.velocity.y == 0.0 || abs(mob.location.y - target.location.y) <= 1) {
                    if (mob.location.distanceSquared(target.location) > 9) {
                        beginBash()
                    } else prepareDive()
                    return
                }
            }
            Action.BASH -> {
                if (bashLeft-- <= 0 || dist < 2.0 || mob.velocity.x == 0.0 || mob.velocity.z == 0.0) {
                    prepareDive()
                    currentAction = Action.FLY
                    bashLeft = bashDuration
                    mob.stopAnimation("bash")
                }
            }
        }
    }

    override fun executeWhenCooledDown() {
        restartCooldown()
        val target = mob.target ?: return

        when (currentAction) {
            Action.FLY, Action.DIVE -> {
                mob.lookAt(target)
                val l = (mob.target?.location ?: return).apply {
                    if(currentAction == Action.FLY) add(0.0, diveHeight, 0.0)
                }
                nmsEntity.moveControl.setWantedPosition(l.x, l.y, l.z, 1.0)
            }
            Action.BASH -> {
                mob.velocity = mob.velocity.setX(bashVelX).setZ(bashVelZ)
            }
        }
    }

    private fun pickDiveHeight() = Random.nextDouble(minHeight, maxHeight)

    private fun prepareDive() {
        currentAction = Action.FLY
        val target = mob.target ?: return

        diveHeight = pickDiveHeight()

        val l = target.location.apply { y += diveHeight }
        nmsEntity.moveControl.setWantedPosition(l.x, l.y, l.z, 1.0)
    }

    private fun beginDive() {
        currentAction = Action.DIVE
        val target = mob.target ?: return
        val l = target.location
        nmsEntity.moveControl.setWantedPosition(l.x, l.y, l.z, 1.0)
        mob.velocity = mob.velocity.setY(-abs(diveVelocity))
    }

    private fun beginBash() {
        currentAction = Action.BASH
        val (x, y, z) = mob.location
        mob.lookAt(x + bashVelX, y, z + bashVelZ)
        bashVelX = mob.location.direction.x * bashVelMultiplier
        bashVelZ = mob.location.direction.z * bashVelMultiplier
        mob.pathfinder.stopPathfinding()
        mob.playAnimation("bash", 0, 0, 1.0)
    }

    private enum class Action {
        FLY, DIVE, BASH
    }
}
