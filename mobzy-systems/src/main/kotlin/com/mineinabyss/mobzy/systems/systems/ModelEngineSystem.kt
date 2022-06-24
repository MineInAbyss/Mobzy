package com.mineinabyss.mobzy.systems.systems

import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.context.GearyContext
import com.mineinabyss.geary.helpers.systems
import com.mineinabyss.geary.papermc.GearyMCContextKoin
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.GearySystem
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.ecs.components.initialization.ModelEngineComponent
import com.mineinabyss.mobzy.modelengine.AnimationController
import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.manager.ModelManager
import com.ticxo.modelengine.api.model.ModeledEntity
import org.bukkit.event.Listener


object ModelEngineSystem : GearySystem, Listener, AnimationController, GearyContext by GearyMCContextKoin() {
    private val modelManager: ModelManager? by lazy {
        runCatching { ModelEngineAPI.api.modelManager }.getOrNull()
    }

    override fun onStart() {
        systems(ModelEngineTracker())
    }

    override fun isModelEngineEntity(entity: BukkitEntity) = entity.toModelEntity() != null

    fun BukkitEntity.toModelEntity(): ModeledEntity? = modelManager?.getModeledEntity(uniqueId)

    override fun playAnimation(entity: BukkitEntity, state: String, lerpIn: Int, lerpOut: Int, speed: Double) =
        entity.toModelEntity()?.allActiveModel?.values
            ?.forEach { it.addState(state, lerpIn, lerpOut, speed) }

    override fun stopAnimation(entity: BukkitEntity, state: String, ignoreLerp: Boolean) =
        entity.toModelEntity()?.allActiveModel?.values
            ?.forEach { it.removeState(state, ignoreLerp) }

    class ModelEngineTracker : GearyListener() {
        val TargetScope.bukkit by onSet<BukkitEntity>()
        val TargetScope.model by onSet<ModelEngineComponent>()

        @Handler
        fun TargetScope.registerModelEngine() {
            val modelEntity = bukkit.toModelEntity() ?: modelManager?.createModeledEntity(bukkit) ?: return

            val createdModel = modelManager?.createActiveModel(model.modelId)?.apply {
                setDamageTint(model.damageTint)
            }
            modelEntity.addActiveModel(createdModel)

            modelEntity.apply {
                bukkit.customName?.let {
                    modelEntity.nametagHandler.setCustomName("head", it)
                }

                modelEntity.nametagHandler.setCustomNameVisibility("head", model.nametag)
                isInvisible = model.invisible
            }
        }
    }
}
