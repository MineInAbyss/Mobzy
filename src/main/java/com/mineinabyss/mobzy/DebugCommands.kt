package com.mineinabyss.mobzy

import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.mobzy.registration.MobzyTypeRegistry
import com.mineinabyss.mobzy.spawning.vertical.VerticalSpawn

internal fun Command.createDebugCommands() {
    "pdc" {
        playerAction {
            sender.sendMessage(
                player.location
                    .getNearbyEntities(5.0, 5.0, 5.0).first()
                    .persistentDataContainer.keys.toString()
            )
        }
    }
    ("configinfo" / "cfginfo")(desc = "Information about the current state of the plugin")?.action {
        sender.info(
            """
            LOG OF CURRENTLY REGISTERED STUFF:
            Spawn configs: ${MobzyConfig.spawnCfgs}
            Registered addons: ${MobzyConfig.registeredAddons}
            Registered EntityTypes: ${MobzyTypeRegistry.typeNames}""".trimIndent()
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
