package com.mineinabyss.mobzy

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.components.addComponents
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.EngineImpl
import com.mineinabyss.geary.ecs.engine.entity
import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.arguments.stringArg
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.idofront.plugin.getService
import com.mineinabyss.looty.ecs.components.Inventory
import com.mineinabyss.looty.ecs.components.PlayerComponent
import com.mineinabyss.looty.ecs.components.PotionComponent
import com.mineinabyss.looty.ecs.components.Screaming
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.store.encodeComponents
import com.mineinabyss.mobzy.registration.MobzyTypeRegistry
import com.mineinabyss.mobzy.spawning.vertical.VerticalSpawn
import net.minecraft.server.v1_16_R2.ItemStack as NMSItemStack

private val inventoryCache = mutableListOf<NMSItemStack>()

internal fun Command.createDebugCommands() {
    ("configinfo" / "cfginfo")(desc = "Information about the current state of the plugin")?.action {
        sender.info(("""
                            LOG OF CURRENTLY REGISTERED STUFF:
                            Spawn configs: ${MobzyConfig.spawnCfgs}
                            Registered addons: ${MobzyConfig.registeredAddons}
                            Registered EntityTypes: ${MobzyTypeRegistry.typeNames}""".trimIndent()))
    }
    "registerself" {
        playerAction {
            Engine.entity {
                addComponents(setOf(PlayerComponent(player.uniqueId), Inventory()))
            }
        }
    }
    fun Command.components(vararg components: GearyComponent) {
        playerAction {
            player.inventory.itemInMainHand.apply {
                itemMeta = itemMeta.apply {
                    persistentDataContainer.encodeComponents(components.toSet())
                }
            }
        }
    }

    "screamingitem" {
        components(Screaming())
    }
    "speeditem" {
        components(PotionComponent("SPEED", 2))
    }

    "components"{
        val type by stringArg()
        action {
            (getService<Engine>() as EngineImpl).bitsets.forEach { (t, u) ->
                if (t.simpleName == type) {
                    var sum = 0
                    u.forEachBit { sum++ }
                    sender.info("$sum entities with that component")
                }
            }
        }
    }
    "inventory" {
        //moving an item in inventory creates a new instance even in NMS!
        "addcache" {
            playerAction {
                inventoryCache.add(player.toNMS().inventory.itemInHand)
            }
        }
        "checkcache" {
            playerAction {
                inventoryCache.forEach { item ->
                    sender.info(when (player.toNMS().inventory.contents.contains(item)) {
                        true -> "${item.name} in inventory"
                        false -> "${item.name} no longer in inventory"
                    })
                }
            }
        }
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