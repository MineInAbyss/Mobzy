package com.mineinabyss.mobzy.modelengine.intializers

import com.mineinabyss.geary.helpers.with
import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.idofront.util.randomOrMin
import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.utils.data.io.SavedData
import org.bukkit.Color
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.world.EntitiesLoadEvent

class ModelEngineWorldListener : Listener {
    @EventHandler
    fun EntitiesLoadEvent.onLoad() {
        entities.forEach { entity ->
            entity.toGearyOrNull()?.with { model: SetModelEngineModel ->
                ensureModelLoaded(entity, model)
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun CreatureSpawnEvent.onSpawn() {
        entity.toGearyOrNull()?.with { model: SetModelEngineModel ->
            ensureModelLoaded(entity, model)
        }
    }

    companion object {
        /** Idempotent function that makes sure the correct model is loaded on an entity. */
        fun ensureModelLoaded(bukkit: BukkitEntity, model: SetModelEngineModel) {
            if (bukkit.isDead) return

            // MEG persists models by default, this ensures any geary entities are handling models on their own
            if (bukkit.persistentDataContainer.has(SavedData.DATA_KEY))
                bukkit.persistentDataContainer.remove(SavedData.DATA_KEY)
            val modelEntity = ModelEngineAPI.getOrCreateModeledEntity(bukkit)
            val blueprint = ModelEngineAPI.getBlueprint(model.modelId)
                ?: error("No blueprint registered for ${model.modelId}")

            // Clear any old models
            val existingMatchedModel = modelEntity.models.toList()
                .onEach { (key, value) ->
                    if (value.blueprint == blueprint) return@onEach
                    modelEntity.removeModel(key)
                    value.destroy()
                }
                .find { (_, value) -> value.blueprint == blueprint }

            val activeModel = (existingMatchedModel?.second ?: ModelEngineAPI.createActiveModel(blueprint)).apply {
                if (model.damageTint) damageTint = Color.RED
                //TODO keep a fixed random value!
                val scale = model.scale.randomOrMin()
                setScale(scale)
                setHitboxScale(scale)
            }

            modelEntity.apply {
                setSaved(false)
                if (existingMatchedModel == null) addModel(activeModel, true).ifPresent { it.destroy() }
                this.base.maxStepHeight = model.stepHeight
                isBaseEntityVisible = !model.invisible
                base.bodyRotationController.rotationDuration = 20
                base.data?.let { data ->
                    model.verticalCull?.let {
                        data.verticalCull = true
                        data.verticalCullType = it.type
                        data.verticalCullDistance = it.distance
                    }
                    model.backCull?.let {
                        data.backCull = true
                        data.backCullAngle = it.angle
                        data.backCullType = it.type
                        data.backCullIgnoreRadius = it.ignoreRadius
                    }
                    model.blockedCull?.let {
                        data.blockedCull = true
                        data.blockedCullType = it.type
                        data.blockedCullIgnoreRadius = it.ignoreRadius
                    }
                }
            }
        }
    }
}
