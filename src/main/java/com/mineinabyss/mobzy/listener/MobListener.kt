package com.mineinabyss.mobzy.listener

import com.mineinabyss.idofront.entities.leftClicked
import com.mineinabyss.idofront.entities.rightClicked
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.mobs.behaviours.HitBehaviour
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.mobzy.toNMS
import net.minecraft.server.v1_15_R1.EntityHuman
import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.Statistic
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerStatisticIncrementEvent
import org.bukkit.event.world.ChunkLoadEvent

class MobListener : Listener {
    /**
     * We use this method to prevent any entity related statistics if they are from our custom mobs, since it causes
     * server crashes. This is mainly used to prevent entity killing players statistic changes, since player killing
     * entity statistics are already handled by the custom mobs' die() methods (if those are handled here, they seem to
     * still cause errors sometimes).
     *
     * @param e the event
     */
    @EventHandler
    fun onStatisticIncrement(e: PlayerStatisticIncrementEvent) { //if the statistic is entity related and the entity is null, it must be custom, therefore we cancel the event
        if (e.statistic.type == Statistic.Type.ENTITY && e.entityType == null) {
            e.isCancelled = true
            //            CustomMobsAPI.debug(ChatColor.RED + "Overrode statistic");
        }
    }

    /**
     * Switch to the hit model of the entity, then shortly after, back to the normal one to create a hit effect
     *
     * @param e the event
     */
    @EventHandler(ignoreCancelled = true)
    fun onHit(e: EntityDamageEvent) {
        val entity = (e.entity as CraftEntity).handle
        if (entity is HitBehaviour) { //change the model to its hit version
            val modelID = (entity as CustomMob).template.modelID
            val ee = (entity.bukkitEntity as LivingEntity).equipment ?: return
            ee.helmet = ee.helmet?.editItemMeta {
                setCustomModelData(modelID + 2)
            }

            //in 5 ticks change the model back to the non hit version
            Bukkit.getScheduler().runTaskLater(mobzy, Runnable {
                if (!entity.bukkitEntity.isDead)
                    ee.helmet = ee.helmet?.editItemMeta {
                        setCustomModelData(modelID)
                    }
            }, 5)
        }
    }

    /**
     * Remove all old entities, which still contain the "customMob" tag (now "customMob2")
     */
    @EventHandler
    fun onChunkLoad(e: ChunkLoadEvent) {
        e.chunk.entities.filter { it.scoreboardTags.contains("customMob") }.forEach { it.remove() }
    }

    /**
     * The magic method that lets you hit entities in their server side hitboxes
     * TODO this doesn't work in adventure mode, but the alternative is a lot worse to deal with. Decide what to do.
     *
     * @param e the event
     */
    @EventHandler
    fun onLeftClick(e: PlayerInteractEvent) { // TODO I'd like some way to ignore hits onto the disguised entity. Perhaps use a marker armorstand?
        val p = e.player
        if (e.leftClicked || e.rightClicked) {
            val trace = p.world.rayTrace(p.eyeLocation, p.location.direction, 3.0, FluidCollisionMode.ALWAYS, true, 0.0) { entity: Entity -> entity != p }
            if (trace != null && trace.hitEntity != null) {
                val hit = trace.hitEntity!!.toNMS()
                if (hit !is CustomMob) return
                if (e.leftClicked) {
                    e.isCancelled = true
                    (p as CraftPlayer).handle.attack(hit)
                } else {
                    val nmsPlayer: EntityHuman = (p as CraftPlayer).handle
                    (hit as CustomMob).onRightClick(nmsPlayer)
                }
            }
        }
    }
}