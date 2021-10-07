package com.mineinabyss.mobzy.ecs.systems

import com.mineinabyss.geary.minecraft.events.GearyMinecraftSpawnEvent
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.ecs.components.initialization.ModelEngineComponent
import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.manager.ModelManager
import com.ticxo.modelengine.api.model.ModeledEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener


object ModelEngineSystem : Listener {
    private val modelManager: ModelManager? by lazy { ModelEngineAPI.api.modelManager }

    fun BukkitEntity.toModelEntity(): ModeledEntity? = modelManager.getModeledEntity(uniqueId)

    @EventHandler
    fun GearyMinecraftSpawnEvent.registerModelEngine() {
        val model = entity.get<ModelEngineComponent>() ?: return
        val bukkit = entity.get<BukkitEntity>() ?: return
        val modelEntity = bukkit.toModelEntity() ?: modelManager?.createModeledEntity(bukkit) ?: return

        val createdModel = modelManager?.createActiveModel(model.modelId)?.apply {
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
