package com.mineinabyss.mobzy.modelengine.intializers

import com.github.shynixn.mccoroutine.bukkit.launch
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.idofront.util.randomOrMin
import com.mineinabyss.mobzy.mobzy
import com.ticxo.modelengine.api.ModelEngineAPI
import kotlinx.coroutines.yield
import org.bukkit.Color
import com.ticxo.modelengine.api.utils.data.io.SavedData as ModelEngineSaveData

class SetModelEngineModelSystem : GearyListener() {
    val Pointers.bukkit by get<BukkitEntity>().whenSetOnTarget()
    val Pointers.model by get<SetModelEngineModel>().whenSetOnTarget()

    override fun Pointers.handle() {
        val bukkit = bukkit
        val model = model
        mobzy.plugin.launch {
            yield() // Wait till next tick so some entity stuff gets initialized
            if (bukkit.isDead) return@launch

            // MEG persists models by default, this ensures any geary entities are handling models on their own
            if (bukkit.persistentDataContainer.has(ModelEngineSaveData.DATA_KEY))
                bukkit.persistentDataContainer.remove(ModelEngineSaveData.DATA_KEY)
            val modelEntity = ModelEngineAPI.getOrCreateModeledEntity(bukkit)
            val blueprint =
                ModelEngineAPI.getBlueprint(model.modelId) ?: error("No blueprint registered for ${model.modelId}")

            val createdModel = ModelEngineAPI.createActiveModel(blueprint).apply {
                if (model.damageTint) damageTint = Color.RED
                val scale = model.scale.randomOrMin()
                setScale(scale)
                setHitboxScale(scale)
            }

            modelEntity.apply {
                setSaved(false)
                addModel(createdModel, true).ifPresent { it.destroy() }
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
