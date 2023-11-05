package com.mineinabyss.mobzy.pathfinding.custom.selectors

import com.mineinabyss.idofront.nms.aliases.NMSLivingEntity
import com.mineinabyss.idofront.nms.aliases.NMSPlayer
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.pathfinding.MobzyPathfinderGoal
import com.mineinabyss.mobzy.pathfinding.components.PathfinderComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityTargetEvent

/**
 * This PathFinderComponent class allows mobs to Target Nearby Players that meet the necessary criteria.
 * Specifically, it builds the following Goal class, which encloses the actual logic of the targetting
 *
 * @param range the range of the mob to detect players to target
 * @param ticksWaitAfterPlayerDeath the number of ticks to wait after a player has died until they are a viable target
 */
@Serializable
@SerialName("mobzy:target.nearest_player")
class TargetNearestPlayer(
//    val target: Class<out NMSLivingEntity>,
    val randomInterval: Int = 10,
    val checkVisiblity: Boolean = true,
    val checkCanNavigate: Boolean = false,
) : PathfinderComponent() {
    override fun build(mob: Mob) =
        NearestAttackableTargetGoal(
            mob.toNMS(),
            NMSPlayer::class.java,
            randomInterval,
            checkVisiblity,
            checkCanNavigate,
            null
        )
}
