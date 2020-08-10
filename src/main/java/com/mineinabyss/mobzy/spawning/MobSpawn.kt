package com.mineinabyss.mobzy.spawning

import com.mineinabyss.mobzy.MobzyConfig
import com.mineinabyss.mobzy.api.nms.entity.creatureType
import com.mineinabyss.mobzy.api.nms.entity.keyName
import com.mineinabyss.mobzy.api.nms.typeinjection.spawnEntity
import com.mineinabyss.mobzy.registration.MobzyRegistry
import com.mineinabyss.mobzy.spawning.vertical.SpawnArea
import com.mineinabyss.mobzy.spawning.vertical.checkDown
import com.mineinabyss.mobzy.spawning.vertical.checkUp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.server.v1_16_R1.EntityTypes
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.util.Vector
import kotlin.math.ceil
import kotlin.math.sign
import kotlin.random.Random
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible

/**
 * A class describing information about
 *
 * @property entityType The the type of entity to spawn.
 * @property minAmount The minimum number of entities to spawn.
 * @property maxAmount The maximum number of entities to spawn.
 * @property radius The radius in which to spawn multiple entities in. Will randomly distribute them within the radius.
 * @property basePriority The base importance of this spawn. Increasing it will increase the likelihood of this spawn to
 * be chosen when compared to other spawns.
 * @property minTime The minimum time of day during which this mob can spawn.
 * @property minTime The maximum time of day during which this mob can spawn.
 * @property minLight The minimum light level required for the mob to spawn.
 * @property maxLight The maximum light level required for the mob to spawn.
 * @property minY The minimum Y level this mob can spawn at.
 * @property maxY The maximum Y level this mob can spawn at.
 * @property minGap The minimum air gap of blocks required for this mob to spawn.
 * @property maxGap The maximum air gap of blocks required for this mob to spawn.
 * @property maxLocalGroup The maximum number of entities of this type that can spawn within the [localGroupRadius].
 * @property localGroupRadius The radius for which the [maxLocalGroup] acts in.
 * @property spawnPos Whether the mob should be spawned directly on the ground, in air, or under cliffs.
 * @property [blockWhitelist] The list of blocks on top of which this mob can spawn.
 */
@Serializable
data class MobSpawn(
        @SerialName("reuse") private val _reuse: String? = null,
        @SerialName("mob") private val _entityTypeName: String? = null,
        @SerialName("min-amount") private val _minAmount: Int? = null,
        @SerialName("max-amount") private val _maxAmount: Int? = null,
        @SerialName("radius") private val _radius: Double? = null,
        @SerialName("priority") private val _basePriority: Double? = null,
        @SerialName("min-time") private val _minTime: Long? = null,
        @SerialName("max-time") private val _maxTime: Long? = null,
        @SerialName("min-light") private val _minLight: Long? = null,
        @SerialName("max-light") private val _maxLight: Long? = null,
        @SerialName("min-y") private val _minY: Int? = null,
        @SerialName("max-y") private val _maxY: Int? = null,
        @SerialName("min-gap") private val _minGap: Int? = null,
        @SerialName("max-gap") private val _maxGap: Int? = null,
        @SerialName("max-local-group") private val _maxLocalGroup: Int? = null,
        @SerialName("local-group-radius") private val _localGroupRadius: Double? = null,
        @SerialName("spawn-pos") private val _spawnPos: SpawnPosition? = null,
        @SerialName("block-whitelist") private val _blockWhitelist: List<Material>? = null
) {

    @Transient
    val copyFrom: MobSpawn? = _reuse?.let { SpawnRegistry.findMobSpawn(it) }

    //TODO try to get a cleaner way to mark these as Transient
    @Transient
    val reuse: String? = _reuse

    @Transient
    val entityTypeName: String? = +MobSpawn::_entityTypeName

    @Transient
    val minAmount: Int = +MobSpawn::_minAmount ?: 1

    @Transient
    val maxAmount: Int = +MobSpawn::_maxAmount ?: 1

    @Transient
    val radius: Double = +MobSpawn::_radius ?: 0.0

    @Transient
    val basePriority: Double = +MobSpawn::_basePriority ?: 1.0

    @Transient
    val minTime: Long = +MobSpawn::_minTime ?: -1L

    @Transient
    val maxTime: Long = +MobSpawn::_maxTime ?: 10000000L

    @Transient
    val minLight: Long = +MobSpawn::_minLight ?: 0L

    @Transient
    val maxLight: Long = +MobSpawn::_maxLight ?: 100L

    @Transient
    val minY: Int = +MobSpawn::_minY ?: 0

    @Transient
    val maxY: Int = +MobSpawn::_maxY ?: 256

    @Transient
    val minGap: Int = +MobSpawn::_minGap ?: 0

    @Transient
    val maxGap: Int = +MobSpawn::_maxGap ?: 256

    @Transient
    val maxLocalGroup: Int = +MobSpawn::_maxLocalGroup ?: -1

    @Transient
    val localGroupRadius: Double = +MobSpawn::_localGroupRadius ?: 10.0

    @Transient
    val spawnPos: SpawnPosition = +MobSpawn::_spawnPos ?: SpawnPosition.GROUND

    @Transient
    val blockWhitelist: List<Material> = +MobSpawn::_blockWhitelist ?: listOf()

    @Transient
    val entityType: EntityTypes<*> = entityTypeName?.let { MobzyRegistry[it] } ?: EntityTypes.ZOMBIE

    private val amountRange: IntRange get() = minAmount..maxAmount
    private val timeRange: LongRange get() = minTime..maxTime
    private val lightRange: LongRange get() = minLight..maxLight
    private val yRange: IntRange get() = minY..maxY
    private val gapRange: IntRange get() = minGap..maxGap

    /** Given a [SpawnArea] Spawns a number of entities defined in this [MobSpawn] at its location */
    fun spawn(area: SpawnArea, spawns: Int = chooseSpawnAmount()): Int {
        val loc = area.getSpawnLocation(spawnPos)
        for (i in 0 until spawns) {
            if (radius != 0.0 && spawnPos != SpawnPosition.AIR)
                (getSpawnInRadius(loc, radius) ?: loc).spawnEntity(entityType)
            else loc.spawnEntity(entityType)
            //TODO could be a better way of handling mobs spawning with too little space (in getPriority) but this works well enough for now
            /*if (!enoughSpace(loc, nmsEntity.width, nmsEntity.length)) { //length is actually the height, don't know why, it's just how it be
                MobzyAPI.debug(ChatColor.YELLOW + "Removed " + ((CustomMob) nmsEntity).getBuilder().getName() + " because of lack of space");
                nmsEntity.die();
            }*/
        }
        return spawns
    }

    /** Gets the priority of this [MobSpawn]. If it is negative, the spawn should never succeed. The higher the
     * priority, the higher the chance of this spawn being picked. */
    fun getPriority(spawnArea: SpawnArea, entityTypeCounts: Map<String, Int>, creatureTypeCounts: Map<String, Int>, playerCount: Int): Double {
        if (spawnArea.gap !in gapRange) return -1.0

        val loc = spawnArea.getSpawnLocation(spawnPos)
        val priority = basePriority
        val time = loc.world!!.time
        val lightLevel = loc.block.lightLevel.toInt()

        //eliminate impossible spawns
        if (time !in timeRange || lightLevel !in lightRange || loc.blockY !in yRange) return -1.0
        if (blockWhitelist.isNotEmpty() && !blockWhitelist.contains(loc.clone().add(0.0, -1.0, 0.0).block.type)) return -1.0

        if (maxLocalGroup > 0) entityTypeCounts[entityType.keyName]?.let {
            //TODO probably rename maxLocalGroup to maxSpawnsPerPlayer
            if (it > maxLocalGroup * playerCount) return -1.0
        }
        creatureTypeCounts[entityType.creatureType.toString()]?.let {
            if (it > MobzyConfig.getMobCap(entityType.creatureType) * playerCount) return -1.0
        }

        //TODO count number of entities around this spawn and prevent it depending on maxLocalGroup
        /*if (maxLocalGroup > 0) {
            runInRadius(localGroupRadius) {
            }
        }*/

        return priority
    }

    fun chooseSpawnAmount(): Int =
            if (minAmount >= maxAmount) minAmount else (Math.random() * (maxAmount - minAmount + 1)).toInt() + minAmount

    /**
     * Where we should look for a location to actually spawn mobs in when calling [spawn]
     *
     * @see SpawnArea.getSpawnLocation
     */
    @Serializable
    enum class SpawnPosition {
        AIR, GROUND, OVERHANG
    }

    /**
     * Checks if there is enough space to spawn an entity in a given location without it suffocating
     * TODO currently gives many false positives
     *
     * @param loc    the location to check
     * @param width  the width of the entity
     * @param height the height of the entity
     * @return whether it will spawn without suffocating
     */
    private fun enoughSpace(loc: Location, width: Double, height: Double): Boolean {
        //TODO convert triple while loop to idofront's pretty function for this
        val checkRad = width / 2
        var y = 0
        while (y < ceil(height)) {
            var x = -checkRad
            while (x < checkRad) {
                var z = -checkRad
                while (z < checkRad) {
                    val checkBlock = loc.clone().add(x, y.toDouble(), z).block
                    if (checkBlock.type.isOccluding) {
                        return false
                    }
                    z++
                }
                x++
            }
            y++
        }
        return true
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

    private operator fun <T> KProperty1<MobSpawn, T>.unaryPlus(): T {
        this.isAccessible = true
        val thisProp = this.get(this@MobSpawn)
        return if (copyFrom != null && thisProp == null)
            this.get(copyFrom)
        else
            thisProp
    }
}