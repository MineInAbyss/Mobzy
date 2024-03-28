@file:UseSerializers(IntRangeSerializer::class)

package com.mineinabyss.mobzy.spawning


import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.papermc.tracking.entities.helpers.spawnFromPrefab
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.serialization.serializers.InnerSerializer
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
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sign
import kotlin.random.Random

@Serializable(with = WGRegions.Serializer::class)
class WGRegions(
    val keys: Set<String>
) {
    class Serializer : InnerSerializer<Set<String>, WGRegions>(
        "mobzy:spawn.regions",
        SetSerializer(String.serializer()),
        { WGRegions(it) },
        { it.keys },
    )
}

@Serializable(with = SpawnSpread.Serializer::class)
class SpawnSpread(
    val radius: Double,
) {
    class Serializer : InnerSerializer<Double, SpawnSpread>(
        "mobzy:spawn.spread",
        Double.serializer(),
        { SpawnSpread(it) },
        { it.radius },
    )
}

@Serializable(with = SpawnAmount.Serializer::class)
class SpawnAmount(
    val amount: IntRange,
) {
    class Serializer : InnerSerializer<IntRange, SpawnAmount>(
        "mobzy:spawn.amount",
        IntRangeSerializer,
        { SpawnAmount(it) },
        { it.amount },
    )
}

@Serializable(with = SpawnType.Serializer::class)
class SpawnType(
    val prefab: PrefabKey,
) {
    class Serializer : InnerSerializer<PrefabKey, SpawnType>(
        "mobzy:spawn.type",
        PrefabKey.serializer(),
        { SpawnType(it) },
        { it.prefab },
    )
}

@Serializable(with = SpawnPriority.Serializer::class)
class SpawnPriority(
    val priority: Double,
) {
    class Serializer : InnerSerializer<Double, SpawnPriority>(
        "mobzy:spawn.priority",
        Double.serializer(),
        { SpawnPriority(it) },
        { it.priority },
    )
}

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
)

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
    val config = mobzySpawning.config
    repeat(spawns) {
        val chosenLoc =
            if (spawnPos != SpawnPosition.AIR)
                getSpawnInRadius(location, radius) ?: location
            else location

        val prefab = type.prefab.toEntity()
        chosenLoc.spawnFromPrefab(prefab).onSuccess { spawned ->
            if (spawned !is LivingEntity || !config.preventSpawningInsideBlock) return@onSuccess
            val bb = spawned.boundingBox
            // We shrink the box by a bit since overlap checks are strict inequalities
            val bbShrunk = spawned.boundingBox.apply {
                expand(-0.1, -0.1, -0.1, -0.1, -0.1, -0.1)
            }

            repeat(config.retriesUpWhenInsideBlock + 1) { offsetY ->
                checkLoop@ for (x in floor(bb.minX).toInt()..ceil(bb.maxX).toInt())
                    for (y in floor(bb.minY).toInt()..ceil(bb.maxY).toInt())
                        for (z in floor(bb.minZ).toInt()..ceil(bb.maxZ).toInt())
                            if (chosenLoc.world.getBlockAt(x, y, z).collisionShape.boundingBoxes.any { shape ->
                                    shape.shift(x.toDouble(), y.toDouble(), z.toDouble())
                                    shape.overlaps(bbShrunk)
                                }) {
                                bb.shift(0.0, 1.0, 0.0)
                                bbShrunk.shift(0.0, 1.0, 0.0)
                                return@repeat
                            }
                if (offsetY != 0) {
                    spawned.teleport(chosenLoc.clone().add(0.0, offsetY.toDouble(), 0.0))
                }
                return@onSuccess
            }
            spawned.remove()
        }
    }
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
