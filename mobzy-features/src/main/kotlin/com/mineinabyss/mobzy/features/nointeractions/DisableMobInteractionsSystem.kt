package com.mineinabyss.mobzy.features.nointeractions

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
import com.mineinabyss.idofront.typealiases.BukkitEntity
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

@AutoScan
class DisableMobInteractionsSystem : GearyListener(), Listener {
    val Pointers.bukkit by get<BukkitEntity>().whenSetOnTarget()
    val Pointers.cancel by get<DisableMobInteractions>().whenSetOnTarget()

    override fun Pointers.handle() {
        bukkit.isInvulnerable = true
        bukkit.setGravity(false)
    }

    @EventHandler
    fun EntityMoveEvent.cancelMovement() {
        if (!hasChangedPosition()) return
        if (entity.toGearyOrNull()?.has<DisableMobInteractions>() != true) return
        isCancelled = true
    }
}
