package com.mineinabyss.mobzy.spawning.vertical

import org.bukkit.Location
import org.bukkit.Material

class VerticalSpawn(private val originalLoc: Location, private var minY: Int, private var maxY: Int) {
    val spawnAreas: List<SpawnArea> = findBlockPairs(originalLoc)

    private fun findBlockPairs(loc: Location): List<SpawnArea> { //boundaries
        if (maxY > 256) maxY = 256
        if (minY < 0) minY = 0

        val locations: MutableList<SpawnArea> = mutableListOf()
        val highest = loc.getHighestBlock(minY, maxY)

        if (highest.blockY < maxY) { //if there's a gap with the sky, add the highest block and current
            if (highest.blockY == minY) //if we found void, return an empty list
                return locations
            //add the gap between the top and highest block (i.e. open sky)
            locations.add(SpawnArea(highest.clone().apply { y = 256.0 }, highest.clone().add(0.0, 1.0, 0.0)))
        } else  //if there was no gap, there won't be any!
            return locations

        //search for gaps and add them to the list as we go down
        var searchL = highest.clone().add(0.0, -1.0, 0.0) //location for searching downwards
        var top = searchL //the top block of a section
        while (searchL.blockY > minY) {
            val prevBlock = searchL.block
            searchL = searchL.add(0.0, -1.0, 0.0)
            val nextBlock = searchL.block
            if (!prevBlock.isPassable && nextBlock.isPassable) //if went from solid to air
                top = searchL.clone()
            else if (prevBlock.isPassable && (!nextBlock.isPassable || searchL.blockY == 1)) //if went back to solid or reached the bottom of the world
                locations.add(SpawnArea(top, searchL.clone().add(0.0, 1.0, 0.0)))
        }
        return locations
    }

    companion object {
        fun checkDown(originalL: Location, maxI: Int): Location? {
            var l = originalL.clone()
            for (i in 0 until maxI) {
                l = l.add(0.0, -1.0, 0.0)
                if (l.y < 10) return null
                if (l.y >= 256) l.y = 255.0
                if (l.block.type.isSolid) return l.add(0.0, 1.0, 0.0)
            }
            return null
        }

        fun checkUp(originalL: Location, maxI: Int): Location? {
            var l = originalL.clone()
            for (i in 0 until maxI) {
                l = l.add(0.0, 1.0, 0.0)
                if (!l.block.type.isSolid) {
                    return l
                }
                if (l.y >= 256) return null
                if (l.y < 10) l.y = 10.0
            }
            return null
        }
    }
}

fun Location.getHighestBlock(minY: Int, maxY: Int): Location { //make a copy of the given location so we don't change its coords.
    val highest = clone()
    highest.y = maxY.toDouble()
    while (highest.block.isPassable && highest.block.type != Material.WATER && highest.blockY > minY) highest.add(0.0, -1.0, 0.0)
    return highest
}