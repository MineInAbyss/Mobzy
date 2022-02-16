package com.mineinabyss.mobzy.systems.systems

import com.mineinabyss.geary.papermc.events.GearyMinecraftSpawnEvent
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.ecs.components.initialization.ModelEngineComponent
import com.mineinabyss.mobzy.ecs.components.interaction.Rideable
import com.mineinabyss.mobzy.modelengine.AnimationController
import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.manager.ModelManager
import com.ticxo.modelengine.api.model.ModeledEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener


object ModelEngineSystem : Listener, AnimationController {
    val modelManager: ModelManager? by lazy {
        runCatching { ModelEngineAPI.api.modelManager }.getOrNull()
    }

    override fun isModelEngineEntity(entity: BukkitEntity) = entity.toModelEntity() != null

    fun BukkitEntity.toModelEntity(): ModeledEntity? = modelManager?.getModeledEntity(uniqueId)

    override fun playAnimation(entity: BukkitEntity, state: String, lerpIn: Int, lerpOut: Int, speed: Double) =
        entity.toModelEntity()?.allActiveModel?.values
            ?.forEach { it.addState(state, lerpIn, lerpOut, speed) }

    override fun stopAnimation(entity: BukkitEntity, state: String, ignoreLerp: Boolean) =
        entity.toModelEntity()?.allActiveModel?.values
            ?.forEach { it.removeState(state, ignoreLerp) }

    // TODO Make the saddle-bone spawn invisible
    // This is changed somewhere after this, as it returns false here.
    @EventHandler
    fun GearyMinecraftSpawnEvent.registerModelEngine() {
        val model = entity.get<ModelEngineComponent>() ?: return
        val bukkit = entity.get<BukkitEntity>() ?: return
        val rideable = entity.get<Rideable>()
        val modelEntity = bukkit.toModelEntity() ?: modelManager?.createModeledEntity(bukkit) ?: return

        val createdModel = modelManager?.createActiveModel(model.modelId)?.apply {
            setDamageTint(model.damageTint)
        }
        modelEntity.addActiveModel(createdModel)
        modelEntity.apply {
            detectPlayers()

            modelEntity.nametagHandler.setCustomName("tag_nametag", bukkit.customName)
            modelEntity.nametagHandler.setCustomNameVisibility("tag_nametag", model.nametag)
            isInvisible = model.invisible
            //createdModel?.partEntities?.get("saddle")?.setItemVisibility(false)
            //createdModel?.partEntities?.get("saddle")?.isVisible.broadcastVal()
        }
    }
}
