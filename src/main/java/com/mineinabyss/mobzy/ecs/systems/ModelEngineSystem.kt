package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.geary.ecs.components.with
import com.mineinabyss.geary.minecraft.components.toBukkit
import com.mineinabyss.geary.minecraft.events.GearyMinecraftSpawnEvent
import com.mineinabyss.mobzy.ecs.components.ModelEngineComponent
import com.ticxo.modelengine.api.ModelEngineAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener


private val modelManager get() = ModelEngineAPI.api.modelManager

class ModelEngineSystem : Listener {
    @EventHandler
    fun GearyMinecraftSpawnEvent.registerModelEngine() {
        entity.with<ModelEngineComponent> { model ->
            val bukkit = entity.toBukkit() ?: return@with
            val modelEntity = modelManager.getModeledEntity(bukkit.uniqueId) ?: modelManager.createModeledEntity(bukkit)

            val createdModel = modelManager.createActiveModel(model.modelId).apply {
                setDamageTint(model.damageTint)
            }
            modelEntity.addActiveModel(createdModel)

            modelEntity.apply {
                detectPlayers()
                bukkit.customName?.let {
                    setNametag(it)
                }

                setNametagVisible(model.nametag)
                isRideable = model.rideable
                isInvisible = model.invisible
            }
        }
    }
}
