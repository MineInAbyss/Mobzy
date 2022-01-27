package com.mineinabyss.mobzy.pathfinding

import com.mineinabyss.geary.minecraft.access.toGeary
import com.mineinabyss.idofront.nms.aliases.NMSPathfinderGoal
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.nms.entity.distanceSqrTo
import com.mineinabyss.idofront.nms.pathfindergoals.PathfinderGoal
import com.mineinabyss.mobzy.ecs.components.initialization.MobAttributes
import net.minecraft.world.entity.EntityInsentient
import net.minecraft.world.entity.ai.control.ControllerMove
import org.bukkit.GameMode
import org.bukkit.Statistic
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import java.util.*

/**
 * @param cooldown How long to wait in ticks before executing [executeWhenCooledDown].
 * Remember to call [restartCooldown] to execute at intervals.
 * @param type It appears once a pathfinder of a certain type has been triggered, no more pathfinders of that
 * type will be executed until it finishes. Need to do more digging in NMS to confirm...
 */
//TODO multiple types at a time
abstract class MobzyPathfinderGoal(private val cooldown: Long = 500, type: Type? = null) : PathfinderGoal() {
    abstract val mob: Mob
    protected val nmsEntity: EntityInsentient by lazy { mob.toNMS() }
    protected val moveController: ControllerMove get() = nmsEntity.controllerMove
    protected val navigation by lazy { mob.toNMS().navigation }

    init {
        if (type != null) setType(type)
    }

    private var cooldownStart: Long = 0

    fun restartCooldown() {
        cooldownStart = System.currentTimeMillis()
    }

    protected val cooledDown get() = cooldownStart < System.currentTimeMillis() - cooldown

    override fun init() = Unit

    override fun reset() = Unit

    override fun execute() = Unit

    override fun e() {
        super.e()
        if (cooledDown) executeWhenCooledDown()
    }

    open fun executeWhenCooledDown() = Unit


    /**
     * This function checks a given player is a viable target for a mob
     *
     * @param player the player to be checked
     * @param range mob's target range / follow range
     * @param ticksWaitAfterPlayerDeath the number of ticks to wait after a player has died until they are a viable target
     * @return True if the player is valid target, False if not
     * */
    fun isPlayerValidTarget(player: Player, range: Double = mob.toGeary().get<MobAttributes>()?.followRange ?: 0.0, ticksWaitAfterPlayerDeath: Int): Boolean {
        if (player.getStatistic(Statistic.TIME_SINCE_DEATH) < ticksWaitAfterPlayerDeath) {    //time in ticks
            return false
        }

        return !player.isInvulnerable &&
                !player.isDead &&
                player.gameMode != GameMode.SPECTATOR &&
                player.gameMode != GameMode.CREATIVE &&
                mob.distanceSqrTo(player) < range * range
    }
}

fun NMSPathfinderGoal.setType(type: net.minecraft.world.entity.ai.goal.PathfinderGoal.Type) = a(EnumSet.of(type))
