package com.mineinabyss.mobzy.modelengine.animation

import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.modelengine.toModelEntity
import com.ticxo.modelengine.api.animation.ModelState
import com.ticxo.modelengine.api.animation.handler.AnimationHandler


class ModelEngineAnimationController : AnimationController {
    override fun isModelEngineEntity(entity: BukkitEntity) = entity.toModelEntity() != null

    override fun playAnimation(
        entity: BukkitEntity,
        stateName: String,
        lerpIn: Double,
        lerpOut: Double,
        speed: Double,
        force: Boolean
    ) {
        entity.toModelEntity()?.models?.values?.forEach {
            val state = ModelState.get(stateName) ?: return
            val defaultProperty = AnimationHandler.DefaultProperty(state, stateName, lerpIn, lerpOut, speed)
            it.animationHandler.setDefaultProperty(defaultProperty)
        }
    }

    override fun stopAnimation(entity: BukkitEntity, state: String, ignoreLerp: Boolean) =
        entity.toModelEntity()?.models?.values
            ?.forEach { it.animationHandler.getDefaultProperty(ModelState.get(state)) }
}
