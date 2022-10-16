package com.mineinabyss.mobzy

import com.mineinabyss.idofront.messaging.logInfo
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.GlobalContext

val globalMobzyConfig = GlobalContext.get().get<MobzyConfig>()

/** Gets [MobzyPlugin] via Bukkit once, then sends that reference back afterwards */
val mobzy: JavaPlugin by lazy { Bukkit.getPluginManager().getPlugin("Mobzy") as JavaPlugin }

fun <T> T.debugVal(message: String = ""): T = debug("$message $this").let { this }

/**
 * Broadcast a message if the debug option is enabled in config
 *
 * @param message the message to be sent
 */
fun debug(message: Any, colorChar: Char? = null) {
    // get global koin
    if (globalMobzyConfig.debug) logInfo(message)
}
