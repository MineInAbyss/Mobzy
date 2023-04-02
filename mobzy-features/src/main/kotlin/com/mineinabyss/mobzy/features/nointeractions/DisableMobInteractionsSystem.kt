package com.mineinabyss.mobzy.features.nointeractions

import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.typealiases.BukkitEntity
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

@AutoScan
class DisableMobInteractionsSystem : GearyListener(), Listener {
    val TargetScope.bukkit by onSet<BukkitEntity>()
    val TargetScope.cancel by onSet<DisableMobInteractions>()

    @Handler
    fun TargetScope.handle() {
        bukkit.isInvulnerable = true
        val armorstand = bukkit as? ArmorStand ?: return
        armorstand.isMarker = true
        armorstand.setGravity(false)
    }

    @EventHandler
    fun EntityMoveEvent.cancelMovement() {
        // For some reason this event seems to be called even for invalid entities??
        if (entity.isValid && !entity.toGeary().has<DisableMobInteractions>()) return
        isCancelled = true
    }
}
