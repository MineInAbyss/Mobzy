package com.mineinabyss.mobzy

import com.mineinabyss.idofront.messaging.logInfo
import com.mineinabyss.mobzy.api.isCustomEntity
import com.mineinabyss.mobzy.mobs.CustomEntity
import org.bukkit.Chunk

fun <T> T.debugVal(message: String = ""): T = debug("$message $this").let { this }

/**
 * Broadcast a message if the debug option is enabled in config
 *
 * @param message the message to be sent
 */
fun debug(message: Any, colorChar: Char? = null) {
    if (mobzyConfig.debug) logInfo(message, colorChar)
}

/** A list of all the [CustomEntity]s in these chunks. */
val List<Chunk>.customMobs get() = flatMap { it.customEntities }

/** A list of all the [CustomEntity]s in this chunk. */
val Chunk.customEntities get() = entities.filter { it.isCustomEntity }
