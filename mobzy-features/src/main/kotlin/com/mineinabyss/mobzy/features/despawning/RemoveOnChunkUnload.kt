package com.mineinabyss.mobzy.features.despawning

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * > mobzy:remove_on_chunk_unload
 *
 * Specifies this entity should be completely removed when the chunk it is in unloads.
 */
@Serializable
@SerialName("mobzy:remove_on_chunk_unload")
data class RemoveOnChunkUnload(
    val keepIfRenamed: Boolean = true
)
