package com.mineinabyss.mobzy

import kotlinx.serialization.Serializable

/**
 * @property debug whether the plugin is in a debug state (used primarily for broadcasting messages)
 * @property doMobSpawns whether custom mob spawning enabled
 */
@Serializable
class MobzyConfig(
    val debug: Boolean = false,
    val supportNonMEEntities: Boolean = false,
)
