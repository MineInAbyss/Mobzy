package com.mineinabyss.mobzy.systems.systems

import com.github.shynixn.mccoroutine.bukkit.launch
import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.context.GearyContext
import com.mineinabyss.geary.helpers.systems
import com.mineinabyss.geary.papermc.GearyMCContextKoin
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.GearySystem
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.messaging.serialize
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.ecs.components.initialization.ModelEngineComponent
import com.mineinabyss.mobzy.mobzy
import com.mineinabyss.mobzy.modelengine.AnimationController
import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.model.ModeledEntity
import com.ticxo.modelengine.api.model.mananger.ModelRegistry
import kotlinx.coroutines.yield
import org.bukkit.Color
import org.bukkit.event.Listener


object ModelEngineSystem : GearySystem, Listener, AnimationController, GearyContext by GearyMCContextKoin() {
    private val modelManager: ModelRegistry? by lazy {
        runCatching { ModelEngineAPI.api.modelRegistry }.getOrNull()
    }

    override fun onStart() {
        systems(ModelEngineTracker())
    }

    override fun isModelEngineEntity(entity: BukkitEntity) = entity.toModelEntity() != null

    fun BukkitEntity.toModelEntity(): ModeledEntity? = ModelEngineAPI.getModeledEntity(uniqueId)

    override fun playAnimation(
        entity: BukkitEntity,
        state: String,
        lerpIn: Double,
        lerpOut: Double,
        speed: Double,
        force: Boolean
    ): Unit? =
        entity.toModelEntity()?.models?.values?.forEach {
            it.animationHandler.playAnimation(state, lerpIn, lerpOut, speed, force)
        }

    override fun stopAnimation(entity: BukkitEntity, state: String, ignoreLerp: Boolean) =
        entity.toModelEntity()?.models?.values
            ?.forEach { it.animationHandler.stopAnimation(state) }

    class ModelEngineTracker : GearyListener() {
        val TargetScope.bukkit by onSet<BukkitEntity>()
        val TargetScope.model by onSet<ModelEngineComponent>()

        @Handler
        fun TargetScope.registerModelEngine() {
            mobzy.launch {
                yield() // Wait till next tick so some entity stuff gets initialized
                val modelEntity = ModelEngineAPI.getOrCreateModeledEntity(bukkit)

                val createdModel =
                    ModelEngineAPI.createActiveModel(ModelEngineAPI.getBlueprint(model.modelId)).apply {
                        if (model.damageTint) rendererHandler.setColor(Color.RED)
                    }

                modelEntity.apply {
                    addModel(createdModel, false)
                    bukkit.customName()?.let {
                        modelEntity.getModel(model.modelId).nametagHandler.bones["nametag"]?.apply {
                            customName = it.serialize()
                            isCustomNameVisible = model.nametag
                        }
                    }
                    isBaseEntityVisible = !model.invisible
                }
            }
        }
    }
}
