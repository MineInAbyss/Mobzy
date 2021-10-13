package com.mineinabyss.mobzy.systems.systems

import com.mineinabyss.geary.minecraft.events.GearyMinecraftSpawnEvent
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.ecs.components.initialization.ModelEngineComponent
import com.mineinabyss.mobzy.modelengine.AnimationController
import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.manager.ModelManager
import com.ticxo.modelengine.api.model.ModeledEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener


object ModelEngineSystem : Listener, AnimationController {
    private val modelManager: ModelManager? by lazy {
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
            isInvisible = model.invisible
        }
    }
}
