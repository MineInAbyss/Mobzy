package com.mineinabyss.mobzy.features.bossbar

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.geary.systems.RepeatingSystem
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.entities.toPlayer
import com.mineinabyss.idofront.typealiases.BukkitEntity
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
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

    fun EntityDeathEvent.onDeath() {
        val bossBar = entity.toGeary().get<DisplayBossBar>() ?: return
        bossBar.playersInRange.forEach {
            it.toPlayer()?.hideBossBar(bossBar.bossBar)
        }
    }

    @EventHandler
    fun EntityDamageByEntityEvent.onDamage() {
        val bossBar = entity.toGeary().get<DisplayBossBar>()
        bossBar?.updateHealth(entity as? LivingEntity ?: return)
    }

    companion object {
        fun DisplayBossBar.updateHealth(bukkit: LivingEntity) {
            bossBar.progress(
                (bukkit.health / (bukkit.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0)).toFloat()
            )
        }
    }
}
