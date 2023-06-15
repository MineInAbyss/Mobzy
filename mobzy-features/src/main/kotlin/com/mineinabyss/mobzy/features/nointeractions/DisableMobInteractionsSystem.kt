package com.mineinabyss.mobzy.features.nointeractions

import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.typealiases.BukkitEntity
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

@AutoScan
class DisableMobInteractionsSystem : GearyListener(), Listener {
    val TargetScope.bukkit by onSet<BukkitEntity>()
    val TargetScope.cancel by onSet<DisableMobInteractions>()

    @Handler
    fun TargetScope.handle() {
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
