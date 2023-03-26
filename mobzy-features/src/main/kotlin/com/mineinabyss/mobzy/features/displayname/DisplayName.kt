package com.mineinabyss.mobzy.features.displayname

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

/**
 */
@JvmInline
@Serializable
@SerialName("mo:display_name")
value class DisplayName(val name: String)

