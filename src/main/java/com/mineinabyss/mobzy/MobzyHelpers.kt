package com.mineinabyss.mobzy

import net.minecraft.server.v1_15_R1.EntityLiving
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