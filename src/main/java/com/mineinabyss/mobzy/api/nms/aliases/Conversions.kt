package com.mineinabyss.mobzy.api.nms.aliases

import net.minecraft.server.v1_16_R2.Entity
import net.minecraft.server.v1_16_R2.World
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld
import org.bukkit.craftbukkit.v1_16_R2.entity.*
import org.bukkit.entity.Creature
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player

typealias BukkitWorld = org.bukkit.World
typealias NMSWorld = World

fun BukkitWorld.toNMS(): NMSWorld = (this as CraftWorld).handle
fun NMSWorld.toBukkit(): BukkitWorld = this.world


typealias BukkitEntity = org.bukkit.entity.Entity
typealias NMSEntity = Entity

//common conversions
/** Converts a Bukkit entity to an NMS entity */
fun BukkitEntity.toNMS(): NMSEntity = (this as CraftEntity).handle
fun LivingEntity.toNMS(): NMSEntityLiving = (this as CraftLivingEntity).handle
fun Mob.toNMS(): NMSEntityInsentient = (this as CraftMob).handle
fun Creature.toNMS(): NMSEntityCreature = (this as CraftCreature).handle
fun Player.toNMS(): NMSPlayer = (this as CraftPlayer).handle

fun NMSEntity.toBukkit() = bukkitEntity as BukkitEntity
fun NMSEntityLiving.toBukkit() = bukkitEntity as LivingEntity
fun NMSEntityInsentient.toBukkit() = bukkitEntity as Mob
fun NMSEntityCreature.toBukkit() = bukkitEntity as Creature
fun NMSPlayer.toBukkit() = bukkitEntity as Player

/** Converts to an NMS entity casted to a specified type */
@Suppress("UNCHECKED_CAST")
@JvmName("toNMSWithCast")
fun <T : NMSEntity> org.bukkit.entity.Entity.toNMS(): T = (this as CraftEntity).handle as T