package com.mineinabyss.mobzy.api.nms.aliases

import net.minecraft.server.v1_16_R1.Entity
import net.minecraft.server.v1_16_R1.EntityLiving
import net.minecraft.server.v1_16_R1.World
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity

typealias NMSWorld = World

fun org.bukkit.World.toNMS(): NMSWorld = (this as CraftWorld).handle


typealias BukkitEntity = org.bukkit.entity.Entity

/** Converts a Bukkit entity to an NMS entity */
fun BukkitEntity.toNMS(): NMSEntity = (this as CraftEntity).handle

/** Converts to an NMS entity casted to a specified type */
@Suppress("UNCHECKED_CAST")
@JvmName("toNMSWithCast")
fun <T : NMSEntity> org.bukkit.entity.Entity.toNMS(): T = (this as CraftEntity).handle as T