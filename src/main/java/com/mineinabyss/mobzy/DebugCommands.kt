package com.mineinabyss.mobzy

import com.mineinabyss.geary.ecs.api.engine.Engine
import com.mineinabyss.geary.ecs.api.engine.temporaryEntity
import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.arguments.intArg
import com.mineinabyss.idofront.commands.arguments.stringArg
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.broadcastVal
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.mobzy.spawning.MobCountManager
import com.mineinabyss.mobzy.spawning.PlayerGroups
import com.mineinabyss.mobzy.spawning.SpawnRegistry
import com.mineinabyss.mobzy.spawning.vertical.VerticalSpawn
import org.bukkit.Bukkit
import kotlin.system.measureTimeMillis

fun Int.toChunkLoc() = (this % 16).let { if (it < 0) it + 16 else it }

//TODO move debugging into its own module (perhaps in Geary-addons)
internal fun Command.createDebugCommands() {
    "spawn" {
        "categoryCounts" {
            action {
                sender.info(MobCountManager.categoryCounts)
            }
        }
        "groups" {
            action {
                sender.info(PlayerGroups.group(Bukkit.getOnlinePlayers()))
            }
        }
        "conditions" {
            val spawnName by stringArg()

            playerAction {
                val loc = player.location
                val x = loc.blockX.toChunkLoc()
                val z = loc.blockZ.toChunkLoc()
                val spawnInfo = VerticalSpawn.findGap(
                    player.location.chunk,
                    minY = -256,
                    maxY = 255,
                    x = x,
                    z = z,
                    startY = loc.blockY
                )
                val spawnDef = SpawnRegistry.findMobSpawn(spawnName.replace("_", " "))
                Engine.temporaryEntity { spawnEntity ->
                    spawnEntity.set(spawnInfo)
                    spawnEntity.set(spawnInfo.bottom)
                    spawnEntity.set(spawnDef)
                    player.info(spawnDef.conditions
                        .filter { !it.metFor(spawnEntity) }
                        .map { it::class.simpleName })
                }
                //TODO list all failed conditions
//                SpawnRegistry.findMobSpawn(spawnName).conditionsMet()
            }
        }
        "find" {
            val miny by intArg()
            val maxy by intArg()
            playerAction {
                val loc = player.location
                val (min, max) = VerticalSpawn.findGap(
                    chunk = loc.chunk,
                    minY = miny,
                    maxY = maxy,
                    x = loc.blockX - (loc.chunk.x shl 4),
                    z = loc.blockZ - (loc.chunk.z shl 4),
                    startY = loc.blockY
                )
                sender.info("${min.y} and ${max.y}")
            }
        }
    }
    "pdc" {
        playerAction {
            sender.sendMessage(
                player.getNearbyEntities(5.0, 5.0, 5.0).first()
                    .persistentDataContainer.keys.toString()
            )
        }
    }

    "benchmark" {
        "nearby" {
            val radius by intArg()
            val i by intArg { default = 10000 }
            playerAction {
                measureTimeMillis {
                    val rad = radius.toDouble()
                    repeat(i) {
                        player.getNearbyEntities(rad, rad, rad).count()
                    }
                }.broadcastVal("Took: ")
            }
        }

    }

    "nearby" {
        val rad by intArg()
        playerAction {
            val rad = rad.toDouble()
            sender.info(player.getNearbyEntities(rad, rad, rad).count())
        }
    }
    "snapshot"()?.playerAction {
        val snapshot = player.location.chunk.chunkSnapshot
        val x = (player.location.blockX % 16).let { if (it < 0) it + 16 else it }
        val z = (player.location.blockZ % 16).let { if (it < 0) it + 16 else it }
        player.success("${snapshot.getBlockType(x, player.location.y.toInt() - 1, z)} at $x, $z")
    }
    "nearbyuuid"()?.playerAction {
        player.info(player.getNearbyEntities(5.0, 5.0, 5.0).first().uniqueId.toString())
    }
}
