package com.mineinabyss.mobzy.pathfinders

import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.toNMS
import net.minecraft.server.v1_15_R1.ControllerMove
import net.minecraft.server.v1_15_R1.EntityInsentient
import net.minecraft.server.v1_15_R1.EntityLiving
import org.bukkit.GameMode
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

abstract class MobzyPathfinderGoal(protected val mob: CustomMob, private val cooldown: Long = 500) : PathfinderGoal() {
    protected val entity: LivingEntity = mob.entity.bukkitEntity as LivingEntity
    protected val nmsEntity: EntityInsentient = mob.entity as EntityInsentient
    protected val moveController: ControllerMove
        get() = nmsEntity.controllerMove
    protected val navigation = mob.navigation
    protected var target
        get() = nmsEntity.goalTarget?.living
        set(value) {
            nmsEntity.goalTarget = value?.toNMS<EntityLiving>()
        }

    protected var lastHit: Long = 0
    protected val cooledDown get() = lastHit < System.currentTimeMillis() - cooldown

    override fun init() = Unit

    override fun reset() = Unit

    override fun execute() = Unit

    fun isPlayerValidTarget(player: Player, range: Double = mob.staticTemplate.followRange ?: 0.0) =
            !player.isInvulnerable &&
                    !player.isDead &&
                    player.gameMode != GameMode.SPECTATOR &&
                    player.gameMode != GameMode.CREATIVE &&
                    mob.distanceTo(player) < range
}