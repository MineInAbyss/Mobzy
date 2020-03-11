package com.mineinabyss.mobzy

import com.mineinabyss.idofront.commands.Command.PlayerExecution
import com.mineinabyss.idofront.commands.IdofrontCommandExecutor
import com.mineinabyss.idofront.commands.arguments.*
import com.mineinabyss.idofront.commands.onExecuteByPlayer
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.mobzy.gui.MobzyGUI
import com.mineinabyss.mobzy.mobs.types.FlyingMob
import com.mineinabyss.mobzy.mobs.types.HostileMob
import com.mineinabyss.mobzy.mobs.types.PassiveMob
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class MobzyCommands : IdofrontCommandExecutor(), TabCompleter {
    override val commands = commands(mobzy) {
        command("mobzy", "mz") {
            command("configinfo", "cfginfo", desc = "Information about the current state of the plugin") {
                onExecute {
                    val config = mobzy.mobzyConfig
                    sender.info(("""
                            LOG OF CURRENTLY REGISTERED STUFF:
                            Mob configs: ${config.mobCfgs}
                            Spawn configs: ${config.spawnCfgs}
                            Registered addons: ${config.registeredAddons}
                            Registered EntityTypes: ${mobzy.mobzyTypes.types}""".trimIndent()))
                }
            }

            command("reload", "rl", desc = "Reloads the configuration files") {
                onExecute {
                    mobzy.mobzyConfig.reload()
                    sender.info("Reloaded config files (not necessarily successfully) :p")
                }
            }

            commandGroup {
                val entityType by +StringArg("entity type")
                val radius by +CustomArg<Int>("radius", { arg.toInt() }) {
                    default = 0
                    ensureChangedByPlayer()
                }

                fun PlayerExecution.removeOrInfo(isInfo: Boolean) {
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

                command("remove", "rm", desc = "Removes mobs") { onExecuteByPlayer { removeOrInfo(false) } }

                command("info", "i", desc = "Lists how many mobs are around you") { onExecuteByPlayer { removeOrInfo(true) } }
            }

            command("spawn", "s", desc = "Spawns a custom mob") {
                val mobName by +StringListArg("mob name", options = mobzy.mobzyTypes.types.keys.toList()) {
                    parseErrorMessage = { "No such entity: $it" }
                }
                var numOfSpawns by +IntArg("number of mobs to spawn") { default = 1 }
                onExecuteByPlayer {
                    if (numOfSpawns > MobzyConfig.maxSpawnAmount) numOfSpawns = MobzyConfig.maxSpawnAmount
                    for (i in 0 until numOfSpawns) (sender as Player).location.spawnEntity(mobName)
                }
            }

            command("list", "l", desc = "Lists all custom mob types") {
                onExecute {
                    sender.success("All registered types:\n${mobzy.mobzyTypes.types.keys}")
                }
            }

            command("config", desc = "Configuration options") {
                command("spawns", desc = "Allows editing of spawn config with a GUI") {
                    onExecuteByPlayer {
                        MobzyGUI(player).show(player)
                    }
                }
                command("domobspawns", desc = "Whether custom mobs can spawn with the custom spawning system") {
                    val enabled by +BooleanArg("enabled")

                    onExecute {
                        MobzyConfig.doMobSpawns = enabled
                        sender.success("Config option doMobSpawns has been set to $enabled")
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
                return mobzy.mobzyTypes.types.keys
                        .filter { it.startsWith(args[1].toLowerCase()) }
            } else if (args.size == 3) {
                var min = 1
                try {
                    min = args[2].toInt()
                } catch (e: NumberFormatException) {
                }
                return (min until MobzyConfig.maxSpawnAmount).asIterable()
                        .map { it.toString() }.filter { it.startsWith(min.toString()) }
            }
        if (subCommand in listOf("remove", "rm", "info", "i"))
            if (args.size == 2) {
                val mobs: MutableList<String> = ArrayList()
                mobs.addAll(mobzy.mobzyTypes.types.keys)
                mobs.addAll(listOf("all", "npc", "mob", "named", "passive", "hostile", "flying"))
                return mobs.filter { it.toLowerCase().startsWith(args[1].toLowerCase()) }
            }
        return if (subCommand == "config") listOf("mobs", "spawns", "domobspawns")
                .filter { it.toLowerCase().startsWith(args[1].toLowerCase()) }
        else emptyList()
    }
}