package com.mineinabyss.mobzy.features.prevent.regen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.event.entity.EntityRegainHealthEvent
import java.util.EnumSet

@Serializable
@SerialName("mobzy:prevent.regeneration")
class PreventRegeneration(val reason: Set<EntityRegainHealthEvent.RegainReason> = setOf())
