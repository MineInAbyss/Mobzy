package com.mineinabyss.mobzy.modelengine

import com.mineinabyss.idofront.plugin.getService
import com.mineinabyss.idofront.typealiases.BukkitEntity

/**
 * A service that lets you interact with ModelEngine without depending on it.
 */
interface AnimationController {
    companion object: AnimationController by getService()

    fun isModelEngineEntity(entity: BukkitEntity): Boolean

    fun playAnimation(entity: BukkitEntity, state: String, lerpIn: Int, lerpOut: Int, speed: Double): Unit?

    fun stopAnimation(entity: BukkitEntity, state: String, ignoreLerp: Boolean = true): Unit?
}

val BukkitEntity.isModelEngineEntity get() = AnimationController.isModelEngineEntity(this)

fun BukkitEntity.playAnimation(state: String, lerpIn: Int, lerpOut: Int, speed: Double) =
    AnimationController.playAnimation(this, state, lerpIn, lerpOut, speed)

fun BukkitEntity.stopAnimation(state: String, ignoreLerp: Boolean = true) =
    AnimationController.stopAnimation(this, state, ignoreLerp)
