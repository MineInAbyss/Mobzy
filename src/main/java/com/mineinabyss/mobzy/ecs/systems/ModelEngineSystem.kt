package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.geary.minecraft.events.GearyMinecraftSpawnEvent
import com.mineinabyss.idofront.nms.aliases.BukkitEntity
import com.mineinabyss.mobzy.ecs.components.initialization.ModelEngineComponent
import com.ticxo.modelengine.api.ModelEngineAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener


object ModelEngineSystem : Listener {
    private val modelManager get() = ModelEngineAPI.api.modelManager

    @EventHandler
    fun GearyMinecraftSpawnEvent.registerModelEngine() {
        val model = entity.get<ModelEngineComponent>() ?: return
        val bukkit = entity.get<BukkitEntity>() ?: return
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
