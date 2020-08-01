package com.mineinabyss.mobzy.api.nms.aliases

import net.minecraft.server.v1_16_R1.Entity
import net.minecraft.server.v1_16_R1.World
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity
import org.bukkit.entity.LivingEntity

typealias BukkitWorld = org.bukkit.World
typealias NMSWorld = World

fun BukkitWorld.toNMS(): NMSWorld = (this as CraftWorld).handle
fun NMSWorld.toBukkit(): BukkitWorld = this.world


typealias BukkitEntity = org.bukkit.entity.Entity
typealias NMSEntity = Entity

/** Converts a Bukkit entity to an NMS entity */
fun BukkitEntity.toNMS(): NMSEntity = (this as CraftEntity).handle
val NMSEntityLiving.living get() = this.bukkitEntity as LivingEntity //TODO move

/** Converts to an NMS entity casted to a specified type */
@Suppress("UNCHECKED_CAST")
@JvmName("toNMSWithCast")
fun <T : NMSEntity> org.bukkit.entity.Entity.toNMS(): T = (this as CraftEntity).handle as T