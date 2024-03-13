package com.mineinabyss.mobzy.spawning.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Marks a mob as important and prevents accidental removes though commands and such.
 */
@Serializable
@SerialName("mobzy:important")
class Important
