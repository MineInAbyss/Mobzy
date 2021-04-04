package com.mineinabyss.mobzy

import com.mineinabyss.geary.ecs.components.Expiry
import com.mineinabyss.geary.minecraft.access.geary
import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.arguments.intArg
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.broadcastVal
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.mobzy.ecs.components.initialization.Model
import com.mineinabyss.mobzy.registration.MobzyNMSTypeInjector
import com.mineinabyss.mobzy.spawning.vertical.VerticalSpawn
import kotlin.system.measureTimeMillis

internal fun Command.createDebugCommands() {
    "expire" {
        playerAction {
            geary(player.getNearbyEntities(5.0, 5.0, 5.0).first()).setRelationWithData<Expiry, Model>(Expiry(3000))
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
    "spawnregion"()?.playerAction {
        player.info(VerticalSpawn(player.location, 0, 255).spawnAreas.toString())
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
