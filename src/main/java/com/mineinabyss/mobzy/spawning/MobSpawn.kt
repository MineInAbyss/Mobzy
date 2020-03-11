package com.mineinabyss.mobzy.spawning

import com.mineinabyss.mobzy.name
import com.mineinabyss.mobzy.spawnEntity
import com.mineinabyss.mobzy.spawning.vertical.SpawnArea
import com.mineinabyss.mobzy.spawning.vertical.VerticalSpawn
import com.mineinabyss.mobzy.toEntityType
import net.minecraft.server.v1_15_R1.EntityTypes
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.util.Vector
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sign
import kotlin.random.Random

/**
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
 * @property blockWhitelist The list of blocks on top of which this mob can spawn.
 */
data class MobSpawn(
        var entityType: EntityTypes<*>,
        var minAmount: Int = 1, //TODO use IntRanges for the min/max properties
        var maxAmount: Int = 1,
        var radius: Double = 0.0,
        var basePriority: Double = 1.0,
        var minTime: Long = -1,
        var maxTime: Long = 10000000,
        var minLight: Long = 0,
        var maxLight: Long = 100,
        var minY: Int = 0,
        var maxY: Int = 256,
        var minGap: Int = 0,
        var maxGap: Int = 256,
        var maxLocalGroup: Int = -1,
        var localGroupRadius: Double = 10.0,
        var spawnPos: SpawnPosition = SpawnPosition.GROUND,
        var blockWhitelist: List<Material> = listOf()) : ConfigurationSerializable {
    val amountRange: IntRange get() = minAmount..maxAmount
    val timeRange: LongRange get() = minTime..maxTime
    val lightRange: LongRange get() = minLight..maxLight
    val yRange: IntRange get() = minY..maxY
    val gapRange: IntRange get() = minGap..maxGap

    @JvmOverloads
    fun spawn(area: SpawnArea, spawns: Int = chooseSpawnAmount()): Int {
        val loc = area.getSpawnLocation(spawnPos)
        for (i in 0 until spawns) {
            if (radius != 0.0 && spawnPos != SpawnPosition.AIR)
                (getSpawnInRadius(loc, 0.0, radius) ?: loc).spawnEntity(entityType)
            else loc.spawnEntity(entityType)
            //TODO could be a better way of handling mobs spawning with too little space (in getPriority) but this works well enough for now
            //FIXME
            /*if (!enoughSpace(loc, nmsEntity.width, nmsEntity.length)) { //length is actually the height, don't know why, it's just how it be
                MobzyAPI.debug(ChatColor.YELLOW + "Removed " + ((CustomMob) nmsEntity).getBuilder().getName() + " because of lack of space");
                nmsEntity.die();
            }*/
        }
        return spawns
    }

    fun getPriority(spawnArea: SpawnArea, entityTypeCounts: Map<String, Int>): Double {
        if (spawnArea.gap !in gapRange) return -1.0

        val loc = spawnArea.getSpawnLocation(spawnPos)
        val priority = basePriority
        val time = loc.world!!.time
        val lightLevel = loc.block.lightLevel.toInt()


        //eliminate impossible spawns
        if (time !in timeRange || lightLevel !in lightRange || loc.blockY !in yRange) return -1.0
        if (blockWhitelist.isNotEmpty() && !blockWhitelist.contains(loc.clone().add(0.0, -1.0, 0.0).block.type)) return -1.0

        //if too many entities of the same type nearby
        if (maxLocalGroup > 0) {

            /*runInRadius(localGroupRadius) {
                //TODO count number of entities in this radius
            }*/
            if((entityTypeCounts[entityType.name] ?: 0) > maxLocalGroup) return -1.0
        }
        return priority
    }

    fun chooseSpawnAmount(): Int {
        return if (minAmount >= maxAmount) minAmount else (Math.random() * (maxAmount - minAmount + 1)).toInt() + minAmount
    }

    override fun serialize(): Map<String, Any> {
        return mapOf()
    }

    enum class SpawnPosition {
        AIR, GROUND, OVERHANG
    }

    companion object {
        /**
         * Checks if there is enough space to spawn an entity in a given location without it suffocating
         * TODO currently gives many false positives
         *
         * @param loc    the location to check
         * @param width  the width of the entity
         * @param height the height of the entity
         * @return whether it will spawn without suffocating
         */
        fun enoughSpace(loc: Location, width: Double, height: Double): Boolean {
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
         * @param minRad the minimum radius for the new location to be picked at
         * @param maxRad the maximum radius for the new location to be picked at
         * @return a new position to spawn in
         */
        fun getSpawnInRadius(loc: Location, minRad: Double, maxRad: Double): Location? {
            for (i in 0..29) {
                val y = (Math.random() - 0.5) * maxRad
                //if y is minRad blocks away from player, mobs can spawn directly under or above
                val minRadCalculated = if (abs(y) > minRad) 0.0 else minRad

                val x = sign(Math.random() - 0.5) * Random.nextDouble(minRadCalculated, maxRad)
                val z = sign(Math.random() - 0.5) * Random.nextDouble(minRadCalculated, maxRad)
                if (!loc.chunk.isLoaded) return null
                var searchLoc: Location? = loc.clone()
                searchLoc = searchLoc!!.add(Vector(x, y, z))
                if (!searchLoc.block.type.isSolid) {
                    searchLoc = VerticalSpawn.checkDown(searchLoc, 25)
                    if (searchLoc != null) return searchLoc
                } else {
                    searchLoc = VerticalSpawn.checkUp(searchLoc, 25)
                    if (searchLoc != null) return searchLoc
                }
            }
            return null
        }

        //TODO make these use @SerializableAs https://www.spigotmc.org/threads/tutorial-bukkit-custom-serialization.148781/
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun deserialize(args: Map<String?, Any?>): MobSpawn {
            fun setArg(name: String, setValue: (Any) -> Unit) {
                if (args.containsKey(name))
                    setValue(args[name]
                            ?: error("Failed to parse argument while serializing MobSpawn for property $name"))
            }
            //create a new builder from the MobSpawn found inside of an already created layer
            val spawn = when {
                args.containsKey("reuse") -> SpawnRegistry.reuseMobSpawn(args["reuse"] as String).copy()
                        .apply { setArg("mob") { mob -> entityType = (mob as String).toEntityType() } }
                args.containsKey("mob") -> MobSpawn((args["mob"] as String).toEntityType())
                else -> error("Serialization failed. No `mob` or `reuse` tag is defined in the spawn.")
            }

            with(spawn){
                setArg("min-amount") { minAmount = (it as Number).toInt() }
                setArg("max-amount") { maxAmount = (it as Number).toInt() }

                setArg("priority") { basePriority = (it as Number).toDouble() }

                setArg("min-gap") { minGap = (it as Number).toInt() }
                setArg("max-gap") { maxGap = (it as Number).toInt() }

                setArg("min-light") { minLight = (it as Number).toLong() }
                setArg("max-light") { maxLight = (it as Number).toLong() }

                setArg("min-time") { minTime = (it as Number).toLong() }
                setArg("max-max") { maxTime = (it as Number).toLong() }

                setArg("min-time") { minTime = (it as Number).toLong() }
                setArg("max-time") { maxTime = (it as Number).toLong() }

                setArg("min-y") { minY = (it as Number).toInt() }
                setArg("max-y") { maxY = (it as Number).toInt() }

                setArg("max-local-group") { maxLocalGroup = (it as Number).toInt() }
                setArg("local-group-radius") { localGroupRadius = (it as Number).toDouble() }
                setArg("radius") { radius = (it as Number).toDouble() }
                setArg("spawn-pos") {
                    spawnPos = when (it as String?) {
                        "AIR" -> SpawnPosition.AIR
                        "GROUND" -> SpawnPosition.GROUND
                        "OVERHANG" -> SpawnPosition.OVERHANG
                        else -> SpawnPosition.GROUND
                    }
                }
                setArg("block-whitelist") { value ->
                    spawn.blockWhitelist = (value as List<String>).map { Material.valueOf(it) }
                }
            }

            return spawn
        }
    }
}