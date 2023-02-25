package com.mineinabyss.mobzy.pathfinding

import com.destroystokyo.paper.entity.Pathfinder
import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.idofront.nms.aliases.NMSMob
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.initializers.attributes.MobAttributes
import net.minecraft.world.entity.ai.goal.Goal
import org.bukkit.GameMode
import org.bukkit.Statistic
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import java.util.*

/**
 * @param cooldown How long to wait in ticks before executing [executeWhenCooledDown].
 * Remember to call [restartCooldown] to execute at intervals.
 * @param flags It appears once a pathfinder of a certain type has been triggered, no more pathfinders of that
 * type will be executed until it finishes. Need to do more digging in NMS to confirm...
 */
//TODO multiple types at a time
abstract class MobzyPathfinderGoal(private val cooldown: Long = 500, flags: List<Flag> = listOf()) : Goal() {
    abstract val mob: Mob
    protected val nmsEntity: NMSMob by lazy { mob.toNMS() }

    //    protected val moveController: MoveControl get() = nmsEntity.moveControl
    protected val pathfinder: Pathfinder by lazy { mob.pathfinder }

    final override fun setFlags(controls: EnumSet<Flag>) = super.setFlags(controls)

    init {
        if (flags.isNotEmpty()) setFlags(EnumSet.copyOf(flags.toList()))
    }

    private var cooldownStart: Long = 0

    fun restartCooldown() {
        cooldownStart = System.currentTimeMillis()
    }

    protected val cooledDown get() = cooldownStart < System.currentTimeMillis() - cooldown
    override fun canUse(): Boolean = shouldExecute()
    override fun canContinueToUse(): Boolean = shouldKeepExecuting()
    override fun start() = init()
    override fun stop() = reset()

    override fun tick() {
        super.tick()
        execute()
        if (cooledDown) executeWhenCooledDown()
    }

    abstract fun shouldExecute(): Boolean
    abstract fun shouldKeepExecuting(): Boolean
    open fun init() = Unit
    open fun reset() = Unit
    open fun execute() = Unit

    open fun executeWhenCooledDown() = Unit


    /**
     * This function checks a given player is a viable target for a mob
     *
     * @param player the player to be checked
     * @param range mob's target range / follow range
     * @param ticksWaitAfterPlayerDeath the number of ticks to wait after a player has died until they are a viable target
     * @return True if the player is valid target, False if not
     * */
    fun isPlayerValidTarget(
        player: Player,
        range: Double = mob.toGeary().get<MobAttributes>()?.followRange ?: 0.0,
        ticksWaitAfterPlayerDeath: Int
    ): Boolean {
        if (player.getStatistic(Statistic.TIME_SINCE_DEATH) < ticksWaitAfterPlayerDeath) return false

        return !player.isInvulnerable &&
                !player.isDead &&
                player.gameMode != GameMode.SPECTATOR &&
                player.gameMode != GameMode.CREATIVE &&
                mob.toNMS().distanceToSqr(player.toNMS()) < range * range
    }
}
