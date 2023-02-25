package com.mineinabyss.mobzy.modelengine.animation

import com.mineinabyss.geary.systems.GearySystem
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.modelengine.AnimationController
import com.mineinabyss.mobzy.modelengine.toModelEntity
import org.bukkit.event.Listener


class ModelEngineAnimationController : GearySystem, Listener, AnimationController {
    override fun isModelEngineEntity(entity: BukkitEntity) = entity.toModelEntity() != null

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
}
