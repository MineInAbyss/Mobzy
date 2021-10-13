package com.mineinabyss.mobzy.spawning

import kotlinx.serialization.Serializable

/**
 * Where we should look for a location to actually spawn mobs in when calling [spawn]
 *
 * @see SpawnInfo.getSpawnFor
 */
@Serializable
enum class SpawnPosition {
    AIR, GROUND, OVERHANG
}
