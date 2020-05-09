package com.mineinabyss.mobzy.spawning

import com.mineinabyss.mobzy.api.creatureType
import com.mineinabyss.mobzy.api.typeName
import com.mineinabyss.mobzy.spawning.vertical.SpawnArea
import org.bukkit.Location

//TODO I don't know if it makes a lot of sense to call this an event, or to convert it into a proper
// spigot event, but can't really think of something better to call it.
/**
 * A wrapper for a mob spawn that'll be done later, with a predetermined spawn number, so they can be decided
 * asynchronously then spawned in synchronously afterwards, since you can't spawn asynchronously.
 *
 *
 * @property mobSpawn The [MobSpawn] that will be spawned from.
 * @property area The [SpawnArea] that will be spawned in.
 * @property entityType The name of the entity type that will be spawned.
 * @property creatureType The name of the type of the creature that will be spawned (i.e. MONSTER, ANIMAL, etc...)
 * @property location The location chosed from the [SpawnArea], through [SpawnArea.getSpawnLocation] and [mobSpawn]
 */
class MobSpawnEvent(private val mobSpawn: MobSpawn, private val area: SpawnArea) {
    val spawns: Int = mobSpawn.chooseSpawnAmount()
    val entityType: String get() = mobSpawn.entityType.typeName
    val creatureType: String get() = mobSpawn.entityType.creatureType.toString()
    val location: Location get() = area.getSpawnLocation(mobSpawn.spawnPos)

    /** Will spawn [mobSpawn] in [area], with [spawns] number of spawns*/
    fun spawn() {
        mobSpawn.spawn(area, spawns)
    }
}