package com.mineinabyss.mobzy.pathfinders

import com.mineinabyss.mobzy.api.helpers.entity.distanceSqrTo
import com.mineinabyss.mobzy.api.nms.aliases.NMSPathfinderGoal
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.pathfindergoals.PathfinderGoal
import com.mineinabyss.mobzy.ecs.components.initialization.attributes
import net.minecraft.server.v1_16_R2.ControllerMove
import net.minecraft.server.v1_16_R2.EntityInsentient
import org.bukkit.GameMode
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

    fun isPlayerValidTarget(player: Player, range: Double = mob.attributes?.followRange ?: 0.0) =
        !player.isInvulnerable &&
                !player.isDead &&
                player.gameMode != GameMode.SPECTATOR &&
                player.gameMode != GameMode.CREATIVE &&
                mob.distanceSqrTo(player) < range * range
}

fun NMSPathfinderGoal.setType(type: net.minecraft.server.v1_16_R2.PathfinderGoal.Type) = a(EnumSet.of(type))
