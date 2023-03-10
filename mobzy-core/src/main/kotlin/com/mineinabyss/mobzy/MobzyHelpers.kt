package com.mineinabyss.mobzy

import com.mineinabyss.idofront.messaging.logInfo

fun <T> T.debugVal(message: String = ""): T = debug("$message $this").let { this }

/**
 * Broadcast a message if the debug option is enabled in config
 *
 * @param message the message to be sent
 */
fun debug(message: Any, colorChar: Char? = null) {
    // get global koin
    if (mobzy.config.debug) logInfo(message)
}
