package com.mineinabyss.mobzy.spawning

import com.mineinabyss.geary.ecs.api.conditions.GearyCondition
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.geary.ecs.serialization.Formats
import com.mineinabyss.geary.minecraft.spawnGeary
import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.serialization.IntRangeSerializer
import com.mineinabyss.idofront.util.randomOrMin
import com.mineinabyss.mobzy.spawning.conditions.SpawnCapCondition
import com.mineinabyss.mobzy.spawning.vertical.SpawnInfo
import com.mineinabyss.mobzy.spawning.vertical.checkDown
import com.mineinabyss.mobzy.spawning.vertical.checkUp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.capturedKClass
import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.math.sign
import kotlin.random.Random

/**
 * A class describing information about
 *
 * @property entityType The the type of entity to spawn.
 * @property amount The range of number of entities to spawn.
 * @property radius The radius in which to spawn multiple entities in. Will randomly distribute them within the radius.
 * @property basePriority The base importance of this spawn. Increasing it will increase the likelihood of this spawn to
 * be chosen when compared to other spawns.
 * @property spawnPos Whether the mob should be spawned directly on the ground, in air, or under cliffs.
 * @property approximateLimit An approximate limit to number of mobs of this type that can spawn. There can be at most [amount]-1 additional mobs unless manually summoned.
 */
@Serializable
data class SpawnDefinition(
    @SerialName("reuse") private val _reuse: String? = null,
    @SerialName("mob") private val _prefabKey: PrefabKey? = null,
    @Serializable(with = IntRangeSerializer::class)
    @SerialName("amount") private val _amount: IntRange? = null,
    @SerialName("radius") private val _radius: Double? = null,
    @SerialName("priority") private val _basePriority: Double? = null,
    @SerialName("spawnPos") private val _spawnPos: SpawnPosition? = null,
    @SerialName("approxLimit") private val _approxLimit: Int? = null,
    @SerialName("conditions")
    val _conditions: List<GearyCondition> = listOf(),
    val ignore: List<String> = listOf()
) {
    companion object {
        val defaultConditions = listOf(
            SpawnCapCondition,
//            EnoughSpaceCondition,
        )
        const val NO_LIMIT = -1;
    }

    @Transient
    val copyFrom: SpawnDefinition? = _reuse?.let { SpawnRegistry.findMobSpawn(it) }

    // associateBy ensures we only have one instance of each condition and we take the overridden one
    @Transient
    val conditions: Collection<GearyCondition> =
        ((copyFrom?.conditions ?: defaultConditions) + _conditions)
            .associateBy { it::class }
            .minus(ignore.map {
                Formats.yamlFormat.serializersModule.getPolymorphic(
                    GearyCondition::class,
                    it
                )?.descriptor?.capturedKClass
            })
            .values

    @Transient
    val prefabKey: PrefabKey = getOrCopy { _prefabKey } ?: error("Mob prefab must not be null")

    @Transient
    val prefab: GearyEntity = prefabKey.toEntity() ?: error("Prefab $prefabKey not found")

    @Transient
    val amount: IntRange = getOrCopy { _amount } ?: 1..1

    @Transient
    val approximateLimit: Int = getOrCopy { _approxLimit } ?: NO_LIMIT

    @Transient
    val radius: Double = getOrCopy { _radius } ?: 0.0

    @Transient
    val basePriority: Double = getOrCopy { _basePriority } ?: 1.0

    @Transient
    val spawnPos: SpawnPosition = getOrCopy { _spawnPos } ?: SpawnPosition.GROUND

    @Transient
    val entityType: NMSEntityType<*> = prefab.get<NMSEntityType<*>>()
        ?: error("No entity type found for prefab $prefabKey in mob spawn")

    /** Given a [SpawnInfo] Spawns a number of entities defined in this [SpawnDefinition] at its location */
    fun spawn(spawnInfo: SpawnInfo, spawns: Int = chooseSpawnAmount()): Int {
        val location = spawnInfo.getSpawnFor(spawnPos)
        for (i in 0 until spawns) {
            val chosenLoc = if (radius != 0.0 && spawnPos != SpawnPosition.AIR)
                getSpawnInRadius(location, radius) ?: location
            else location

            chosenLoc.spawnGeary(prefab)
        }
        return spawns
    }

    fun conditionsMet(area: GearyEntity): Boolean {
        return conditions.all { it.metFor(area) }
    }

    fun chooseSpawnAmount(): Int = amount.randomOrMin()

    /**
     * Where we should look for a location to actually spawn mobs in when calling [spawn]
     *
     * @see SpawnInfo.getSpawnFor
     */
    @Serializable
    enum class SpawnPosition {
        AIR, GROUND, OVERHANG
    }

    /**
     * Gets a location to spawn in a mob given an original location and min/max radii around it
     *
     * @param loc    the location to check off of
     * @param maxRad the maximum radius for the new location to be picked at
     * @return a new position to spawn in
     */
    private fun getSpawnInRadius(loc: Location, maxRad: Double): Location? {
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

    // Hacky stuff for the `reuse` keyword
    /** Uses a value from [copyFrom] unless overridden in this spawn. */
    private inline fun <T> getOrCopy(prop: SpawnDefinition.() -> T) =
        this.prop() ?: copyFrom?.prop()
}
