package com.mineinabyss.geary.papermc.systems

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.commons.components.interaction.Attacked
import com.mineinabyss.geary.components.events.EntityRemoved
import com.mineinabyss.geary.datatypes.family.family
import com.mineinabyss.geary.papermc.components.DisplayBossBar
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.RepeatingSystem
import com.mineinabyss.geary.systems.accessors.EventScope
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.entities.toPlayer
import com.mineinabyss.idofront.typealiases.BukkitEntity
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import kotlin.time.Duration.Companion.seconds

/**
 * Handles displaying of boss bars to players in range.
 * Uses values from the DisplayBossBar component.
 */
@AutoScan
class BossBarDisplaySystem : RepeatingSystem(interval = 0.5.seconds), Listener {
    private val TargetScope.bossbar by get<DisplayBossBar>()
    private val TargetScope.bukkitentity by get<BukkitEntity>()

    override fun TargetScope.tick() {
        val bukkit = bukkitentity as? LivingEntity ?: return
        val playersInRange = bukkitentity.getNearbyEntities(bossbar.range, bossbar.range, bossbar.range)
            .filterIsInstance<Player>().mapTo(mutableSetOf()) { it.uniqueId }

        // Gets players to add and remove
        val addPlayers = playersInRange - bossbar.playersInRange
        val removePlayers = bossbar.playersInRange - playersInRange

        // Removes and adds the necessary players
        bossbar.playersInRange.removeAll(removePlayers)
        bossbar.playersInRange.addAll(addPlayers)

        addPlayers.forEach { it.toPlayer()?.showBossBar(bossbar.bossBar) }
        removePlayers.forEach { it.toPlayer()?.hideBossBar(bossbar.bossBar) }
        bossbar.updateHealth(bukkit)
    }

    @AutoScan
    class RemoveBossBarOnDeath : GearyListener() {
        //TODO convert to a component remove listener when those get added
        val TargetScope.bossBar by get<DisplayBossBar>()
        val EventScope.removed by family { has<EntityRemoved>() }

        @Handler
        fun TargetScope.remove() {
            bossBar.playersInRange.forEach {
                it.toPlayer()?.hideBossBar(bossBar.bossBar)
            }
        }
    }

    @AutoScan
    class UpdateBossBarOnDamage : GearyListener() {
        //TODO convert to a component remove listener when those get added
        val TargetScope.bossBar by get<DisplayBossBar>()
        val TargetScope.bukkitEntity by get<BukkitEntity>()
        val EventScope.damaged by family { has<Attacked>() }

        @Handler
        fun TargetScope.update() {
            val living = bukkitEntity as? LivingEntity ?: return
            bossBar.updateHealth(living)
        }
    }


    companion object {
        fun DisplayBossBar.updateHealth(bukkit: LivingEntity) {
            bossBar.progress(
                (bukkit.health / (bukkit.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0)).toFloat()
            )
        }
    }
}
