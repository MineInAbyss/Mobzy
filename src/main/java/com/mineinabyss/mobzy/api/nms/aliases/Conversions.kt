@file:Suppress("NOTHING_TO_INLINE")

package com.mineinabyss.mobzy.api.nms.aliases

import net.minecraft.server.v1_16_R2.Entity
import net.minecraft.server.v1_16_R2.World
import net.minecraft.server.v1_16_R2.WorldServer
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld
import org.bukkit.craftbukkit.v1_16_R2.entity.*
import org.bukkit.entity.*

typealias BukkitWorld = org.bukkit.World
typealias NMSWorld = World
typealias NMSWorldServer = WorldServer

inline fun BukkitWorld.toNMS(): NMSWorldServer = (this as CraftWorld).handle
inline fun NMSWorld.toBukkit(): BukkitWorld = this.world


typealias BukkitEntity = org.bukkit.entity.Entity
typealias NMSEntity = Entity

//common conversions
/** Converts a Bukkit entity to an NMS entity */
inline fun BukkitEntity.toNMS(): NMSEntity = (this as CraftEntity).handle
inline fun LivingEntity.toNMS(): NMSEntityLiving = (this as CraftLivingEntity).handle
inline fun Mob.toNMS(): NMSEntityInsentient = (this as CraftMob).handle
inline fun Creature.toNMS(): NMSEntityCreature = (this as CraftCreature).handle
inline fun HumanEntity.toNMS(): NMSEntityHuman = (this as CraftHumanEntity).handle
inline fun Player.toNMS(): NMSPlayer = (this as CraftPlayer).handle
inline fun Snowball.toNMS(): NMSSnowball = (this as CraftSnowball).handle

inline fun NMSEntity.toBukkit() = bukkitEntity as BukkitEntity
inline fun NMSEntityLiving.toBukkit() = bukkitEntity as LivingEntity
inline fun NMSEntityInsentient.toBukkit() = bukkitEntity as Mob
inline fun NMSEntityCreature.toBukkit() = bukkitEntity as Creature
inline fun NMSEntityHuman.toBukkit() = bukkitEntity as HumanEntity
inline fun NMSPlayer.toBukkit() = bukkitEntity as Player
inline fun NMSSnowball.toBukkit() = bukkitEntity as Snowball

/** Converts to an NMS entity casted to a specified type */
@Suppress("UNCHECKED_CAST")
@JvmName("toNMSWithCast")
inline fun <T : NMSEntity> org.bukkit.entity.Entity.toNMS(): T = (this as CraftEntity).handle as T
