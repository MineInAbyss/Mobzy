package com.offz.spigot.mobzy.spawning

import com.offz.spigot.mobzy.CustomType.Companion.getType
import com.offz.spigot.mobzy.CustomType.Companion.spawnEntity
import com.offz.spigot.mobzy.isCustomMob
import com.offz.spigot.mobzy.spawning.vertical.SpawnArea
import com.offz.spigot.mobzy.spawning.vertical.VerticalSpawn
import com.offz.spigot.mobzy.toNMS
import net.minecraft.server.v1_15_R1.EntityTypes
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.util.Vector
import kotlin.math.abs
import kotlin.math.sign

data class MobSpawn(val entityType: EntityTypes<*>,
                    var minAmount: Int = 1,
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
                    var whitelist: List<Material> = listOf()) : ConfigurationSerializable {

    @JvmOverloads
    fun spawn(area: SpawnArea, spawns: Int = chooseSpawnAmount()): Int {
        val loc = area.getSpawnLocation(spawnPos)
        for (i in 0 until spawns) {
            if (radius == 0.0) {
                spawnEntity(entityType, loc)
            } else {
                if (radius != 0.0 && spawnPos != SpawnPosition.AIR)
                    spawnEntity(entityType, getSpawnInRadius(loc, 0.0, radius) ?: loc)
                else spawnEntity(entityType, loc)
            }
            //TODO could be a better way of handling mobs spawning with too little space (in getPriority) but this works well enough for now
            //FIXME
            /*if (!enoughSpace(loc, nmsEntity.width, nmsEntity.length)) { //length is actually the height, don't know why, it's just how it be
                MobzyAPI.debug(ChatColor.YELLOW + "Removed " + ((CustomMob) nmsEntity).getBuilder().getName() + " because of lack of space");
                nmsEntity.die();
            }*/
        }
        return spawns
    }

    fun getPriority(spawnArea: SpawnArea, toSpawn: List<MobSpawnEvent>): Double {
        return if (spawnArea.gap < minGap || spawnArea.gap > maxGap) (-1).toDouble() else getPriority(spawnArea.getSpawnLocation(spawnPos), toSpawn)
    }

    fun getPriority(loc: Location, toSpawn: List<MobSpawnEvent>): Double {
        val priority = basePriority
        val time = loc.world!!.time
        val lightLevel = loc.block.lightLevel.toInt()

        //eliminate impossible spawns
        if (time !in minTime..maxTime ||
                lightLevel !in minLight..maxLight ||
                loc.blockY !in minY..maxY)
            return -1.0
        if (whitelist.isNotEmpty() && !whitelist.contains(loc.clone().add(0.0, -1.0, 0.0).block.type)) return -1.0

        //if too many entities of the same type nearby
        if (maxLocalGroup > 0) {
            var nearbyEntities = 0
            for (e in loc.world!!.getNearbyEntities(loc, localGroupRadius, localGroupRadius, localGroupRadius)) { //TODO this doesnt factor in planned-to-spawn entities
                if (e.isCustomMob && e.toNMS().entityType == entityType) nearbyEntities++
                if (nearbyEntities >= maxLocalGroup) return -1.0
            }
            for (spawn in toSpawn) {
                if (spawn.location.world == loc.world && spawn.location.distance(loc) < localGroupRadius) nearbyEntities++
                if (nearbyEntities >= maxLocalGroup) return -1.0
            }
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
            while (y < Math.ceil(height)) {
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
            var minRad = minRad
            for (i in 0..29) {
                val y = (Math.random() - 0.5) * maxRad
                if (abs(y) > minRad) //if y is minRad blocks away from player, mobs can spawn directly under or above
                    minRad = 0.0
                val x = sign(Math.random() - 0.5) * (Math.random() * (maxRad - minRad) + minRad)
                val z = sign(Math.random() - 0.5) * (Math.random() * (maxRad - minRad) + minRad)
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
        @JvmStatic
        fun deserialize(args: Map<String?, Any?>): MobSpawn? {
            fun setArg(name: String, setValue: (Any) -> Unit) {
                if (args.containsKey(name))
                    setValue(args[name] ?: error("Failed to parse argument while serializing MobSpawn"))
            }

            val spawn = MobSpawn(entityType = getType((args["mob"] as String)))
            setArg("min-amount") { spawn.minAmount = (it as Number).toInt() }
            setArg("max-amount") { spawn.maxAmount = (it as Number).toInt() }

            setArg("priority") { spawn.basePriority = (it as Number).toDouble() }

            setArg("min-gap") { spawn.minGap = (it as Number).toInt() }
            setArg("max-gap") { spawn.maxGap = (it as Number).toInt() }

            setArg("min-light") { spawn.minLight = (it as Number).toLong() }
            setArg("max-light") { spawn.maxLight = (it as Number).toLong() }

            setArg("min-time") { spawn.minTime = (it as Number).toLong() }
            setArg("max-max") { spawn.maxTime = (it as Number).toLong() }

            setArg("min-time") { spawn.minTime = (it as Number).toLong() }
            setArg("max-time") { spawn.maxTime = (it as Number).toLong() }

            setArg("min-y") { spawn.minY = (it as Number).toInt() }
            setArg("max-y") { spawn.maxY = (it as Number).toInt() }

            setArg("max-local-group") { spawn.maxLocalGroup = (it as Number).toInt() }
            setArg("local-group-radius") { spawn.localGroupRadius = (it as Number).toDouble() }
            setArg("radius") { spawn.radius = (it as Number).toDouble() }
            setArg("spawn-pos") {
                spawn.spawnPos = when (it as String?) {
                    "AIR" -> SpawnPosition.AIR
                    "GROUND" -> SpawnPosition.GROUND
                    "OVERHANG" -> SpawnPosition.OVERHANG
                    else -> SpawnPosition.GROUND
                }
            }
            setArg("block-whitelist") {
                spawn.whitelist = (it as List<String>).map { Material.valueOf(it) }
            }

            return spawn
        }
    }
}