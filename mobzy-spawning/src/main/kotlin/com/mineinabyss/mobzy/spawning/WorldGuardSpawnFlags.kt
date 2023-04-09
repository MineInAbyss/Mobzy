package com.mineinabyss.mobzy.spawning

import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.StringFlag
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException

class WorldGuardSpawnFlags {
    //TODO Make these into their own custom flags instead of StringFlag
    //TODO rename this to MZ_... in the WorldGuard config files :mittysweat:
    var MZ_SPAWN_REGIONS: StringFlag? = null
    var MZ_SPAWN_OVERLAP: StringFlag? = null

    fun registerFlags() {
        val registry = WorldGuard.getInstance().flagRegistry
        val mzSpawnRegions = registry["cm-spawns"]
        val mzSpawnOverlap = registry["mz-spawn-overlap"]
        //register MZ_SPAWN_REGIONS
        if (mzSpawnRegions is StringFlag) //avoid problems if registering flag that already exists
            MZ_SPAWN_REGIONS = mzSpawnRegions
        else try {
            val flag = StringFlag("cm-spawns", "")
            registry.register(flag)
            MZ_SPAWN_REGIONS = flag
        } catch (e: FlagConflictException) {
            e.printStackTrace()
        }
        //register MZ_SPAWN_OVERLAP
        if (mzSpawnOverlap is StringFlag) MZ_SPAWN_OVERLAP = mzSpawnOverlap
        else try {
            val flag = StringFlag("mz-spawn-overlap", "stack")
            registry.register(flag)
            MZ_SPAWN_OVERLAP = flag
        } catch (e: FlagConflictException) {
            e.printStackTrace()
        }
    }

    /*fun disablePluginSpawnsErrorMessage() {
        //TODO try to allow plugin spawning in WorldGuard's config automatically
        //onCreatureSpawn in WorldGuardEntityListener throws errors if we don't enable custom entity spawns
        WorldGuard.getInstance().platform.globalStateManager.get(BukkitAdapter.adapt(server.worlds.first()))
                .blockPluginSpawning = false
    }*/
}
