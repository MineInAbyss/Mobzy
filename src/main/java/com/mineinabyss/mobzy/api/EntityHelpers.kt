@file:JvmMultifileClass
@file:JvmName("MobzyAPI")

package com.mineinabyss.mobzy.api

import com.mineinabyss.mobzy.api.nms.aliases.NMSEntity
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.nms.entity.typeName
import com.mineinabyss.mobzy.mobs.CustomEntity
import com.mineinabyss.mobzy.registration.toEntityTypeName
import org.bukkit.entity.Entity

/** Whether an entity is a renamed mob registered with Mobzy. */
//TODO feels like an unnecessarily specific function?
val Entity.isCustomAndRenamed get() = if (!isCustomEntity || customName == null) false else customName != this.typeName

/** Converts [Entity] to [CustomEntity]. */
fun Entity.toMobzy() = toNMS().toMobzy()

/** Converts [NMSEntity] to [CustomEntity]. */
fun NMSEntity.toMobzy() = this as? CustomEntity

/** @return Whether the mob is of type of the given [typeName]. */
fun Entity.isOfType(typeName: String) = this.typeName == typeName.toEntityTypeName()

/** Whether this is a custom mob registered with Mobzy. */
val Entity.isCustomEntity get() = toNMS().isCustomEntity

/** Whether this is a custom mob registered with Mobzy. */
val NMSEntity.isCustomEntity get() = this is CustomEntity
