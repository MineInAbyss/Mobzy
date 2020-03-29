@file:JvmMultifileClass
@file:JvmName("MobzyAPI")

package com.mineinabyss.mobzy.api

import com.mineinabyss.mobzy.mobs.CustomMob
import com.mineinabyss.mobzy.mobs.MobTemplate
import com.mineinabyss.mobzy.registration.toEntityTypeName
import com.mineinabyss.mobzy.toNMS
import net.minecraft.server.v1_15_R1.EntityTypes
import net.minecraft.server.v1_15_R1.EnumCreatureType
import org.bukkit.entity.Entity
import net.minecraft.server.v1_15_R1.Entity as EntityNMS

/** Whether an entity is a renamed mob registered with Mobzy. */
val Entity.isRenamed get() = if (toNMS().isCustomMob || customName == null) false else customName != this.typeName

/** The mobzy ID for a registered custom mob. */
val Entity.typeName get() = toNMS().entityType.typeName

/** Converts [Entity] to [CustomMob]. */
fun Entity.toMobzy() = toNMS().toMobzy()

/** Converts [EntityNMS] to [CustomMob]. */
fun EntityNMS.toMobzy() = this as CustomMob

/** @return Whether the mob is of type of the given [typeName]. */
fun Entity.isOfType(typeName: String) = this.typeName == typeName.toEntityTypeName()

/** The [EnumCreatureType] for this [EntityTypes] object. */
val EntityTypes<*>.creatureType: EnumCreatureType get() = this.e()

/** The name of the [EnumCreatureType] of this entity. */
val EntityNMS.creatureType get() = this.entityType.creatureType.name

/** Whether this mob's creature type (i.e. monster, creature, water_creature, ambient, misc) is [creatureType] */
fun Entity.isOfCreatureType(creatureType: String) = toNMS().isOfCreatureType(creatureType)

/** Whether this mob's creature type (i.e. monster, creature, water_creature, ambient, misc) is [creatureType] */
fun EntityNMS.isOfCreatureType(creatureType: String) = this.entityType.creatureType.name == creatureType

/** Whether this is a custom mob registered with Mobzy. */
val Entity.isCustomMob get() = toNMS().isCustomMob

/** Whether this is a custom mob registered with Mobzy. */
val EntityNMS.isCustomMob get() = this is CustomMob

/** A custom mob's [MobTemplate] that is registered with Mobzy. */
val Entity.template: MobTemplate get() = toNMS().template

/** A custom mob's [MobTemplate] that is registered with Mobzy. */
val EntityNMS.template: MobTemplate get() = (this as CustomMob).template