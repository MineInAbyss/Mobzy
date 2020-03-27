package com.mineinabyss.mobzy.pathfinders

import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.toNMS
import net.minecraft.server.v1_15_R1.ControllerMove
import net.minecraft.server.v1_15_R1.EntityInsentient
import net.minecraft.server.v1_15_R1.EntityLiving
import org.bukkit.GameMode
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

abstract class MobzyPathfinderGoal(private val cooldown: Long = 500) : PathfinderGoal() {
    abstract val mob: CustomMob
    protected val entity: LivingEntity by lazy { mob.entity.bukkitEntity as LivingEntity }
    protected val nmsEntity: EntityInsentient by lazy { mob.entity as EntityInsentient }
    protected val moveController: ControllerMove get() = nmsEntity.controllerMove
    protected val navigation by lazy { mob.navigation }
    protected var target
        get() = nmsEntity.goalTarget?.living
        set(value) {
            nmsEntity.goalTarget = value?.toNMS<EntityLiving>()
        }

    private var cooldownStart: Long = 0

    fun restartCooldown() {
        cooldownStart = System.currentTimeMillis()
    }

    protected val cooledDown get() = cooldownStart < System.currentTimeMillis() - cooldown

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