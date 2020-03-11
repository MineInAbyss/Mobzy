package com.mineinabyss.mobzy.spawning

import com.mineinabyss.mobzy.name
import com.mineinabyss.mobzy.spawning.vertical.SpawnArea
import org.bukkit.Location

//TODO I don't know if it makes a lot of sense to call this an event, or to convert it into a proper
// spigot event, but can't really think of something better to call it.
/**
 * A wrapper for a mob spawn that'll be done later, with a predetermined spawn number, so they can be decided
 * asynchronously then spawned in synchronously afterwards, since you can't directly interact with Bukkit
 * asynchronously.
 */
class MobSpawnEvent(private val mobSpawn: MobSpawn, private val area: SpawnArea) {
    val spawns: Int = mobSpawn.chooseSpawnAmount()
    val entityType: String
        get() = mobSpawn.entityType.name

    fun spawn() {
        mobSpawn.spawn(area, spawns)
    }

    val location: Location
        get() = area.getSpawnLocation(mobSpawn.spawnPos)

}