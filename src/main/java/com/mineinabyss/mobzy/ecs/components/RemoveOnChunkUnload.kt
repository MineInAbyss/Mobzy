package com.mineinabyss.mobzy.ecs.components

import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * > mobzy:remove_on_chunk_unload
 *
 * Specifies this entity should be completely removed when the chunk it is in unloads.
 */
@Serializable
@SerialName("mobzy:remove_on_chunk_unload")
@AutoscanComponent
data class RemoveOnChunkUnload(
    val keepIfRenamed: Boolean = true
)
