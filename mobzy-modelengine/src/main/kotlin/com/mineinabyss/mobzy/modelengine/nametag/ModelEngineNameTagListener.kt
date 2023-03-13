package com.mineinabyss.mobzy.modelengine.nametag

import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.idofront.textcomponents.serialize
import com.mineinabyss.mobzy.modelengine.intializers.SetModelEngineModel
import com.mineinabyss.mobzy.modelengine.toModelEntity
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

class ModelEngineNameTagListener : Listener {
    @EventHandler
    fun PlayerInteractEntityEvent.nameTagMob() {
        val itemInHand = player.inventory.itemInMainHand
        val gearyEntity = rightClicked.toGearyOrNull() ?: return
        val modelEntity = rightClicked.toModelEntity() ?: return

        if (itemInHand.type == Material.NAME_TAG) {
            modelEntity.getModel(gearyEntity.get<SetModelEngineModel>()?.modelId).nametagHandler.fakeEntity
                .firstNotNullOfOrNull { it.value }?.run {
                    customName = itemInHand.itemMeta.displayName()?.serialize()
                    isCustomNameVisible = true
                }
        }
    }
}
