package com.mineinabyss.mobzy.pathfinders

import com.mineinabyss.mobzy.api.helpers.entity.distanceSqrTo
import com.mineinabyss.mobzy.api.nms.aliases.NMSEntityInsentient
import com.mineinabyss.mobzy.api.nms.aliases.living
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.pathfindergoals.PathfinderGoal
import com.mineinabyss.mobzy.ecs.components.minecraft.attributes
import com.mineinabyss.mobzy.mobs.AnyCustomMob
import net.minecraft.server.v1_16_R1.ControllerMove
import net.minecraft.server.v1_16_R1.EntityInsentient
import net.minecraft.server.v1_16_R1.EntityLiving
import org.bukkit.GameMode
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

abstract class MobzyPathfinderGoal(private val cooldown: Long = 500) : PathfinderGoal() {
    abstract val mob: AnyCustomMob
    protected val entity: LivingEntity by lazy { mob.nmsEntity.bukkitEntity as LivingEntity }
    protected val nmsEntity: EntityInsentient by lazy { mob.nmsEntity }
    protected val moveController: ControllerMove get() = nmsEntity.controllerMove
    protected val navigation by lazy { mob.nmsEntity.navigation }
//    protected var target
//        get() = nmsEntity.goalTarget?.living
//        set(value) {
//            nmsEntity.goalTarget = value?.toNMS<NMSEntityInsentient>()
//        }

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

    fun isPlayerValidTarget(player: Player, range: Double = mob.type.attributes?.followRange ?: 0.0) =
            !player.isInvulnerable &&
                    !player.isDead &&
                    player.gameMode != GameMode.SPECTATOR &&
                    player.gameMode != GameMode.CREATIVE &&
                    mob.entity.distanceSqrTo(player) < range
}