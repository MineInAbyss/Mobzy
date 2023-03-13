package com.mineinabyss.mobzy.modelengine.animation

import com.mineinabyss.idofront.plugin.Services
import com.mineinabyss.idofront.typealiases.BukkitEntity
import com.mineinabyss.mobzy.modelengine.mobzyModelEngine

/**
 * A service that lets you interact with ModelEngine without depending on it.
 */
interface AnimationController {
    fun isModelEngineEntity(entity: BukkitEntity): Boolean

    fun playAnimation(
        entity: BukkitEntity,
        state: String,
        lerpIn: Double,
        lerpOut: Double,
        speed: Double,
        force: Boolean
    ): Unit?

    fun stopAnimation(entity: BukkitEntity, state: String, ignoreLerp: Boolean = true): Unit?
}

val BukkitEntity.isModelEngineEntity: Boolean
    get() = mobzyModelEngine?.animationController?.isModelEngineEntity(this) ?: false

fun BukkitEntity.playAnimation(state: String, lerpIn: Double, lerpOut: Double, speed: Double, force: Boolean) =
    mobzyModelEngine?.animationController?.playAnimation(this, state, lerpIn, lerpOut, speed, force)

fun BukkitEntity.stopAnimation(state: String, ignoreLerp: Boolean = true) =
    mobzyModelEngine?.animationController?.stopAnimation(this, state, ignoreLerp)
