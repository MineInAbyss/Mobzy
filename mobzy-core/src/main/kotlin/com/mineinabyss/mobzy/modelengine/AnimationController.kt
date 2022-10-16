package com.mineinabyss.mobzy.modelengine

import com.mineinabyss.idofront.plugin.Services
import com.mineinabyss.idofront.typealiases.BukkitEntity

/**
 * A service that lets you interact with ModelEngine without depending on it.
 */
interface AnimationController {
    companion object : AnimationController by Services.get()

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
    get() = Services.getOrNull<AnimationController>()?.isModelEngineEntity(this) ?: false

fun BukkitEntity.playAnimation(state: String, lerpIn: Double, lerpOut: Double, speed: Double, force: Boolean) =
    Services.getOrNull<AnimationController>()?.playAnimation(this, state, lerpIn, lerpOut, speed, force)

fun BukkitEntity.stopAnimation(state: String, ignoreLerp: Boolean = true) =
    Services.getOrNull<AnimationController>()?.stopAnimation(this, state, ignoreLerp)
