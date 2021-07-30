package com.mineinabyss.mobzy

import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.arguments.intArg
import com.mineinabyss.idofront.commands.arguments.stringArg
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.broadcastVal
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.mobzy.registration.MobzyNMSTypeInjector
import com.mineinabyss.mobzy.spawning.PlayerGroups
import com.mineinabyss.mobzy.spawning.vertical.VerticalSpawn
import org.bukkit.Bukkit
import kotlin.system.measureTimeMillis

internal fun Command.createDebugCommands() {
    "spawn" {
        "groups" {
            action {
                sender.info(PlayerGroups.group(Bukkit.getOnlinePlayers()))
            }
        }
        "conditions" {
            val spawnName by stringArg()

            playerAction {
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
            val rad by intArg()
            val i by intArg { default = 10000 }
            playerAction {
                measureTimeMillis {
                    val rad = rad.toDouble()
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
    ("configinfo" / "cfginfo")(desc = "Information about the current state of the plugin")?.action {
        sender.info(
            """
            LOG OF CURRENTLY REGISTERED STUFF:
            Spawn configs: ${MobzyConfig.spawnCfgs}
            Registered addons: ${MobzyConfig.registeredAddons}
            Registered EntityTypes: ${MobzyNMSTypeInjector.typeNames}""".trimIndent()
        )
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
