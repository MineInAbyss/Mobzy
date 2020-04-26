package com.mineinabyss.mobzy

import com.mineinabyss.idofront.messaging.logInfo
import com.mineinabyss.mobzy.api.isCustomMob
import com.mineinabyss.mobzy.mobs.CustomMob
import net.minecraft.server.v1_15_R1.EntityLiving
import org.bukkit.Chunk
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity
import org.bukkit.entity.Entity
import net.minecraft.server.v1_15_R1.Entity as EntityNMS

/**
 * Converts a Bukkit entity to an NMS entity
 */
fun Entity.toNMS(): EntityNMS = (this as CraftEntity).handle

/**
 * Converts to an NMS entity casted to a specified type
 */
@Suppress("UNCHECKED_CAST")
@JvmName("toNMSWithCast")
fun <T : EntityLiving> Entity.toNMS(): EntityLiving = (this as CraftEntity).handle as T

fun <T> T.debugVal(message: String = ""): T = debug("$message $this").let { this }

/**
 * Broadcast a message if the debug option is enabled in config
 *
 * @param message the message to be sent
 */
fun debug(message: String, colorChar: Char? = null) {
    if (mobzyConfig.debug) logInfo(message, colorChar)
}

/** A list of all the [CustomMob]s in these chunks. */
val List<Chunk>.customMobs get() = flatMap { it.customMobs }

/** A list of all the [CustomMob]s in this chunk. */
val Chunk.customMobs get() = entities.filter { it.isCustomMob }