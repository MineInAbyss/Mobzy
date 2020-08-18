package com.mineinabyss.mobzy

import com.mineinabyss.idofront.commands.arguments.booleanArg
import com.mineinabyss.idofront.commands.arguments.intArg
import com.mineinabyss.idofront.commands.arguments.optionArg
import com.mineinabyss.idofront.commands.arguments.stringArg
import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.commands.extensions.actions.PlayerAction
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.mobzy.api.isCustomMob
import com.mineinabyss.mobzy.api.isOfType
import com.mineinabyss.mobzy.api.isRenamed
import com.mineinabyss.mobzy.api.nms.aliases.toNMS
import com.mineinabyss.mobzy.api.spawnMobzyMob
import com.mineinabyss.mobzy.mobs.types.FlyingMob
import com.mineinabyss.mobzy.mobs.types.HostileMob
import com.mineinabyss.mobzy.mobs.types.PassiveMob
import com.mineinabyss.mobzy.registration.MobzyTypeRegistry
import com.mineinabyss.mobzy.spawning.vertical.VerticalSpawn
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCommandDSL
object MobzyCommands : IdofrontCommandExecutor(), TabCompleter {
    override val commands = commands(mobzy) {
        command("mobzy", "mz") {
            command("configinfo", "cfginfo", desc = "Information about the current state of the plugin")?.action {
                sender.info(("""
                            LOG OF CURRENTLY REGISTERED STUFF:
                            Spawn configs: ${MobzyConfig.spawnCfgs}
                            Registered addons: ${MobzyConfig.registeredAddons}
                            Registered EntityTypes: ${MobzyTypeRegistry.typeNames}""".trimIndent()))
            }

            command("reload", "rl", desc = "Reloads the configuration files")?.action {
                MobzyConfig.reload(sender)
            }

            commandGroup {
                val entityType by stringArg()
                val radius by intArg() { default = 0 }

                fun PlayerAction.removeOrInfo(isInfo: Boolean) {
                    val worlds = mobzy.server.worlds
                    var mobCount = 0
                    var entityCount = 0

                    for (world in worlds) for (entity in world.entities) {
                        val tags = entity.scoreboardTags
                        val nmsEntity = entity.toNMS()
                        if (entity.isCustomMob && when (entityType) {
                                    "all" -> !entity.isRenamed && !entity.scoreboardTags.contains("npc")
                                    "named" -> entity.isRenamed
                                    "npc" -> entity.scoreboardTags.contains("npc")
                                    "passive" -> !entity.scoreboardTags.contains("npc") && nmsEntity is PassiveMob
                                    "hostile" -> nmsEntity is HostileMob
                                    "flying" -> nmsEntity is FlyingMob
                                    else -> entity.isOfType(entityType)
                                }) {
                            val playerLoc = player.location
                            if (radius <= 0 || entity.world == playerLoc.world && entity.location.distance(playerLoc) < radius) {
                                if (!isInfo) entity.remove() //only kill mobs if command was cmrm and not cminfo
                                entityCount++
                                if (!tags.contains("additionalPart")) mobCount++
                            }
                        }
                    }

                    sender.success((if (isInfo) "There are " else "Removed ") +
                            "&l$mobCount&r&a " + (if (entityType == "all") "custom mobs " else "$entityType ") +
                            (if (entityCount != mobCount) "($entityCount entities) " else "") + //account for multi-entity mobs
                            (if (radius <= 0) "in all loaded chunks." else "in a radius of $radius blocks."), '&'
                    )
                }

                command("remove", "rm", desc = "Removes mobs")?.playerAction {
                    removeOrInfo(false)
                }

                command("info", "i", desc = "Lists how many mobs are around you")?.playerAction {
                    removeOrInfo(true)
                }
            }

            command("spawn", "s", desc = "Spawns a custom mob") {
                val mobName by optionArg(options = MobzyTypeRegistry.typeNames) {
                    parseErrorMessage = { "No such entity: $passed" }
                }
                val numOfSpawns by intArg() {
                    name = "number of spawns"
                    default = 1
                }

                playerAction {
                    val cappedSpawns = numOfSpawns.coerceAtMost(MobzyConfig.maxCommandSpawns)
                    for (i in 1..cappedSpawns) player.location.spawnMobzyMob(mobName)
                }
            }
            command("debug") {
                command("spawnregion")?.playerAction {
                    player.info(VerticalSpawn(player.location, 0, 255).spawnAreas.toString())
                }
                command("snapshot")?.playerAction {
                    val snapshot = player.location.chunk.chunkSnapshot
                    val x = (player.location.blockX % 16).let { if (it < 0) it + 16 else it }
                    val z = (player.location.blockZ % 16).let { if (it < 0) it + 16 else it }
                    player.success("${snapshot.getBlockType(x, player.location.y.toInt() - 1, z)} at $x, $z")
                }
                command("nearbyuuid")?.playerAction {
                    player.info(player.getNearbyEntities(5.0, 5.0, 5.0).first().uniqueId.toString())
                }
            }

            command("list", "l", desc = "Lists all custom mob types")?.action {
                sender.success("All registered types:\n${MobzyTypeRegistry.typeNames}")
            }

            command("config", desc = "Configuration options") {
                command("domobspawns", desc = "Whether custom mobs can spawn with the custom spawning system") {
                    val enabled by booleanArg()

                    //TODO expand for all properties, this will probably be done through MobzyConfig, so `serialized` can
                    // be made private once that's done
                    action {
                        if (MobzyConfig.doMobSpawns != enabled) {
                            MobzyConfig.doMobSpawns = enabled
                            sender.success("Config option doMobSpawns has been set to $enabled")
                        } else
                            sender.success("Config option doMobSpawns was already set to $enabled")
                        mobzy.registerSpawnTask()
                    }
                }
            }
        }
    }

    //TODO make the API do tab completion
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        if (command.name != "mobzy") return emptyList()
        if (args.size <= 1) return listOf("spawn", "info", "remove", "reload", "fullreload", "i", "rm", "s", "config")
                .filter { it.startsWith(args[0]) }
        val subCommand = args[0]
        if (subCommand == "spawn" || subCommand == "s")
            if (args.size == 2) {
                return MobzyTypeRegistry.typeNames
                        .filter { it.startsWith(args[1].toLowerCase()) }
            } else if (args.size == 3) {
                var min = 1
                try {
                    min = args[2].toInt()
                } catch (e: NumberFormatException) {
                }
                return (min until MobzyConfig.maxCommandSpawns).asIterable()
                        .map { it.toString() }.filter { it.startsWith(min.toString()) }
            }
        if (subCommand in listOf("remove", "rm", "info", "i"))
            if (args.size == 2) {
                val mobs: MutableList<String> = ArrayList()
                mobs.addAll(MobzyTypeRegistry.typeNames)
                mobs.addAll(listOf("all", "npc", "mob", "named", "passive", "hostile", "flying"))
                return mobs.filter { it.toLowerCase().startsWith(args[1].toLowerCase()) }
            }
        return if (subCommand == "config") listOf("mobs", "spawns", "domobspawns")
                .filter { it.toLowerCase().startsWith(args[1].toLowerCase()) }
        else emptyList()
    }
}