package com.offz.spigot.mobzy

import com.offz.spigot.mobzy.CustomType.Companion.toEntityTypeID
import com.offz.spigot.mobzy.mobs.CustomMob
import com.offz.spigot.mobzy.mobs.MobTemplate
import net.minecraft.server.v1_15_R1.EntityLiving
import net.minecraft.server.v1_15_R1.EntityTypes
import net.minecraft.server.v1_15_R1.EnumCreatureType
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity
import org.bukkit.entity.Entity
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import net.minecraft.server.v1_15_R1.Entity as EntityNMS

fun <T> T.debugVal(message: String = ""): T {
    debug("$message $this")
    return this
}

fun <T> T.logVal(message: String = ""): T {
    logInfo("$message $this")
    return this
}

/**
 * Broadcast a message if the debug option is enabled in config
 *
 * @param message the message to be sent
 */
fun debug(message: String) {
    if (MobzyConfig.isDebug) Bukkit.broadcastMessage(message)
}

fun logInfo(message: String, color: ChatColor = ChatColor.WHITE) {
    mobzy.logger.info("$color$message")
}

fun logError(message: String) {
    logInfo(message, ChatColor.RED)
}

fun logGood(message: String) {
    logInfo(message, ChatColor.GREEN)
}

fun logWarn(message: String) {
    logInfo(message, ChatColor.YELLOW)
}

/**
 * Registers a separate plugin's the spawn configuration file with the API
 *
 * @param configuration the file in which to look for a configuration
 * @param plugin        the plugin this configuration file corresponds to
 */
fun registerSpawnConfig(configuration: File, plugin: JavaPlugin) {
    mobzy.mobzyConfig.registerSpawnCfg(configuration, plugin)
}

/**
 * Registers a separate plugin's the mob configuration file with the API (to read mob attributes)
 *
 * @param configuration the file in which to look for a configuration
 * @param plugin        the plugin this configuration file corresponds to
 */
fun registerMobConfig(configuration: File, plugin: JavaPlugin) {
    mobzy.mobzyConfig.registerMobCfg(configuration, plugin)
}

/**
 * @param mob the given entity
 * @return whether it is a renamed mob registered with Mobzy
 */
val Entity.isRenamed
    get() = if (this.toNMS().isCustomMob() || customName == null) false else customName != this.mobzyID


/**
 * Converts a Bukkit entity to an NMS entity
 */
fun Entity.toNMS(): EntityNMS = (this as CraftEntity).handle

fun <T : EntityLiving> Entity.toNMS(): EntityLiving = (this as CraftEntity).handle as T

/**
 * The mobzy ID for a registered custom mob
 */
val Entity.mobzyID
    get() = this.toNMS().mobzyID

fun Entity.toMobzy() = this.toNMS().toMobzy()

fun EntityNMS.toMobzy() = this as CustomMob


/**
 * The mobzy ID for a registered custom mob
 */
val EntityNMS.mobzyID
    get() = toEntityTypeID(this.template.name)

/**
 * @return Whether the mob is of type of the given mob ID
 */
fun Entity.isOfType(mobID: String) = this.toNMS().isOfType(mobID)

/**
 * @return Whether the mob is of type of the given mob ID
 */
fun EntityNMS.isOfType(mobID: String) = this.mobzyID == mobID


fun Entity.isOfCreatureType(creatureType: String) = this.toNMS().isOfCreatureType(creatureType)
fun EntityNMS.isOfCreatureType(creatureType: String) = this.entityType.creatureType.name == creatureType

/**
 * @return whether this is a custom mob registered with Mobzy
 */
val Entity.isCustomMob
    get() = this.toNMS().isCustomMob()

/**
 * @return whether this is a custom mob registered with Mobzy
 */
fun EntityNMS.isCustomMob() = this is CustomMob

/**
 * A custom mob's [MobTemplate] that is registered with Mobzy
 */
val Entity.template: MobTemplate
    get() = this.toNMS().template

/**
 * A custom mob's [MobTemplate] that is registered with Mobzy
 */
val EntityNMS.template: MobTemplate
    get() = (this as CustomMob).template

/**
 * @param name the name of an entity type
 * @return its builder
 */
fun getTemplate(name: String): MobTemplate {
    return CustomType.getTemplate(name)
}

val EntityTypes<*>.name: String
    get() = this.f()

val EntityTypes<*>.creatureType: EnumCreatureType
    get() = this.e()