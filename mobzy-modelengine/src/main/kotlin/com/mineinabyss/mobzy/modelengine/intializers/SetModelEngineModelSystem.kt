package com.mineinabyss.mobzy.modelengine.intializers

import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.Pointers
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.idofront.util.randomOrMin
import com.ticxo.modelengine.api.ModelEngineAPI
import org.bukkit.Color

class SetModelEngineModelSystem : GearyListener() {
    val Pointers.bukkit by get<BukkitEntity>().whenSetOnTarget()
    val Pointers.model by get<SetModelEngineModel>().whenSetOnTarget()

    override fun Pointers.handle() {
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
            addModel(createdModel, true)
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
