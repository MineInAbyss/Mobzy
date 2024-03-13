@file:UseSerializers(IntRangeSerializer::class)

package com.mineinabyss.mobzy.spawning


import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.papermc.tracking.entities.helpers.spawnFromPrefab
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.systems.accessors.*
import com.mineinabyss.geary.systems.builders.listener
import com.mineinabyss.geary.systems.query.ListenerQuery
import com.mineinabyss.idofront.serialization.IntRangeSerializer
import com.mineinabyss.idofront.util.randomOrMin
import com.mineinabyss.mobzy.spawning.vertical.SpawnInfo
import com.mineinabyss.mobzy.spawning.vertical.checkDown
import com.mineinabyss.mobzy.spawning.vertical.checkUp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.math.sign
import kotlin.random.Random

@Serializable
@SerialName("mobzy:spawn_regions")
class WGRegions(
    val keys: Set<String>
)

@Serializable
@SerialName("mobzy:spawn.spread")
class SpawnSpread(
    val radius: Double,
)

@Serializable
@SerialName("mobzy:spawn.amount")
class SpawnAmount(
    val amount: IntRange,
)

@Serializable
@SerialName("mobzy:spawn.type")
class SpawnType(
    val prefab: PrefabKey,
)

@Serializable
@SerialName("mobzy:spawn.priority")
class SpawnPriority(
    val priority: Double,
)

/**
 * Where we should look for a location to actually spawn mobs in when calling [spawn]
 *
 * @see SpawnInfo.getSpawnFor
 */
@Serializable
@SerialName("mobzy:spawn.position")
enum class SpawnPosition {
    AIR, GROUND, OVERHANG
}


/**
 * A class describing information about
 *
 * @property entityType The the type of entity to spawn.
 * @property minAmount The minimum number of entities to spawn.
 * @property maxAmount The maximum number of entities to spawn.
 * @property radius The radius in which to spawn multiple entities in. Will randomly distribute them within the radius.
 * @property basePriority The base importance of this spawn. Increasing it will increase the likelihood of this spawn to
 * be chosen when compared to other spawns.
 * @property spawnPos Whether the mob should be spawned directly on the ground, in air, or under cliffs.
 */
data class DoSpawn(
    val location: Location
) {
    var spawnedAmount: Int = 0
}

@AutoScan
fun GearyModule.spawnRequestListener() = listener(object : ListenerQuery() {
    val type by get<SpawnType>()
    val amount by get<SpawnAmount>().orNull().map { it?.amount }
    val spawnPos by get<SpawnPosition>().orDefault { SpawnPosition.GROUND }
    val radius by get<SpawnSpread>().orNull().map { it?.radius ?: 0.0 }

    val spawnEvent by event.get<DoSpawn>()
}).exec {
    val location = spawnEvent.location
    val spawns = amount?.randomOrMin() ?: 1
    for (i in 0 until spawns) {
        val chosenLoc =
            if (spawnPos != SpawnPosition.AIR)
                getSpawnInRadius(location, radius) ?: location
            else location

        chosenLoc.spawnFromPrefab(type.prefab)
    }
    spawnEvent.spawnedAmount = spawns
}


/**
 * Gets a location to spawn in a mob given an original location and min/max radii around it
 *
 * @param loc    the location to check off of
 * @param maxRad the maximum radius for the new location to be picked at
 * @return a new position to spawn in
 */
private fun getSpawnInRadius(loc: Location, maxRad: Double): Location? {
    if (maxRad == 0.0) return loc
    if (!loc.chunk.isLoaded) return null
    for (i in 0..29) { //TODO, arbitrary number, should instead search all locations around the spawn
        val x = sign(Math.random() - 0.5) * Random.nextDouble(maxRad)
        val z = sign(Math.random() - 0.5) * Random.nextDouble(maxRad)
        val searchLoc: Location = loc.clone().add(Vector(x, 0.0, z))

        return if (!searchLoc.block.type.isSolid)
            searchLoc.checkDown(2) ?: continue
        else
            searchLoc.checkUp(2) ?: continue
    }
    return null
}
