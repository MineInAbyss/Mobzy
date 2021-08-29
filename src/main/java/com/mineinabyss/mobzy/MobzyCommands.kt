package com.mineinabyss.mobzy

import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.geary.minecraft.access.geary
import com.mineinabyss.geary.minecraft.spawnGeary
import com.mineinabyss.geary.minecraft.toPrefabKey
import com.mineinabyss.idofront.commands.arguments.booleanArg
import com.mineinabyss.idofront.commands.arguments.intArg
import com.mineinabyss.idofront.commands.arguments.optionArg
import com.mineinabyss.idofront.commands.arguments.stringArg
import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.commands.extensions.actions.PlayerAction
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.nms.entity.typeName
import com.mineinabyss.idofront.nms.entity.typeNamespacedKey
import com.mineinabyss.mobzy.api.isCustomAndRenamed
import com.mineinabyss.mobzy.api.isCustomEntity
import com.mineinabyss.mobzy.api.isOfType
import com.mineinabyss.mobzy.mobs.types.FlyingMob
import com.mineinabyss.mobzy.mobs.types.HostileMob
import com.mineinabyss.mobzy.mobs.types.PassiveMob
import com.mineinabyss.mobzy.registration.MobzyNMSTypeInjector
import com.mineinabyss.mobzy.registration.MobzyTypesQuery
import com.mineinabyss.mobzy.registration.MobzyTypesQuery.key
import com.mineinabyss.mobzy.spawning.SpawnTask
import com.mineinabyss.mobzy.spawning.vertical.categorizeMobs
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Entity

@ExperimentalCommandDSL
class MobzyCommands : IdofrontCommandExecutor(), TabCompleter {
    override val commands = commands(mobzy) {
        ("mobzy" / "mz") {
            ("reload" / "rl")(desc = "Reloads the configuration files") {
                "spawns" {
                    MobzyConfig.reloadSpawns()
                    sender.success("Reloaded spawn config")
                }

                action {
                    MobzyConfig.reload(sender)
                }
            }

            commandGroup {
                val entityType by stringArg()
                val radius by intArg { default = 0 }

                fun PlayerAction.removeOrInfo(isInfo: Boolean) {
                    val worlds = mobzy.server.worlds
                    var mobCount = 0
                    var entityCount = 0
                    val entities = mutableSetOf<Entity>()
                    for (world in worlds) for (entity in world.entities) {
                        val tags = entity.scoreboardTags
                        val nmsEntity = entity.toNMS()
                        if (entity.isCustomEntity && when (entityType) {
                                "all" -> !entity.isCustomAndRenamed && !entity.scoreboardTags.contains("npc")
                                "named" -> entity.isCustomAndRenamed
                                "npc" -> entity.scoreboardTags.contains("npc")
                                "passive" -> !entity.scoreboardTags.contains("npc") && nmsEntity is PassiveMob
                                "hostile" -> nmsEntity is HostileMob
                                "flying" -> nmsEntity is FlyingMob
                                else -> entity.typeNamespacedKey.toPrefabKey() == PrefabKey(entityType)
                            }
                        ) {
                            val playerLoc = player.location
                            if (radius <= 0 || entity.world == playerLoc.world && entity.location.distance(playerLoc) < radius) {
                                entityCount++
                                if (isInfo) entities += entity
                                else entity.remove()
                            }
                        }
                    }

                    sender.success(
                        """
                        ${if (isInfo) "There are" else "Removed"}
                        &l$mobCount&r&a ${if (entityType == "all") "custom mobs" else entityType}
                        ${if (radius <= 0) "in all loaded chunks." else "in a radius of $radius blocks."}
                        """.trimIndent().replace("\n", " "), '&'
                    )
                    if (isInfo) {
                        val categories = entities.categorizeMobs()
                        if (categories.size > 1)
                            sender.info(
                                categories.entries
                                    .sortedByDescending { it.value.get() }
                                    .joinToString("\n") { (type, amount) -> "&7${type.typeName}&r: $amount".color() }
                            )
                    }
                }

                ("remove" / "rm")(desc = "Removes mobs")?.playerAction {
                    removeOrInfo(false)
                }

                ("info" / "i")(desc = "Lists how many mobs are around you")?.playerAction {
                    removeOrInfo(true)
                }
            }

            ("spawn" / "s")(desc = "Spawns a custom mob") {
                val mobKey by optionArg(options = MobzyTypesQuery.map { it.key.toString() }) {
                    parseErrorMessage = { "No such entity: $passed" }
                }
                val numOfSpawns by intArg {
                    name = "number of spawns"
                    default = 1
                }

                playerAction {
                    val cappedSpawns = numOfSpawns.coerceAtMost(mobzyConfig.maxCommandSpawns)
                    val key = PrefabKey(mobKey)

                    repeat(cappedSpawns) {
                        player.location.spawnGeary(key) ?: error("Prefab $mobKey not found")
                    }
                }
            }

            "debug" {
                createDebugCommands()
            }

            ("list" / "l")(desc = "Lists all custom mob types")?.action {
                sender.success("All registered types:\n${MobzyNMSTypeInjector.typeNames}")
            }

            "config"(desc = "Configuration options") {
                command("domobspawns", desc = "Whether custom mobs can spawn with the custom spawning system") {
                    val enabled by booleanArg()

                    //TODO expand for all properties, this will probably be done through MobzyConfig, so `serialized` can
                    // be made private once that's done
                    action {
                        if (mobzyConfig.doMobSpawns != enabled) {
                            mobzyConfig.doMobSpawns = enabled
                            sender.success("Config option doMobSpawns has been set to $enabled")
                        } else
                            sender.success("Config option doMobSpawns was already set to $enabled")
                        if (enabled) SpawnTask.startTask()
                    }
                }
            }
        }
    }

    //TODO make the API do tab completion
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        return when {
            command.name != "mobzy" -> emptyList()
            args.size <= 1 -> listOf(
                "spawn",
                "info",
                "remove",
                "reload",
                "fullreload",
                "i",
                "rm",
                "s",
                "config",
                "stats"
            )
                .filter { it.startsWith(args[0]) }
            else -> {
                val subCommand = args[0]

                if (subCommand == "spawn" || subCommand == "s")
                    if (args.size == 2) {
                        return MobzyTypesQuery
                            .filter {
                                val arg = args[1].toLowerCase()
                                it.key.name.startsWith(arg) || it.key.key.startsWith(arg)
                            }
                            .map { it.key.toString() }
                    } else if (args.size == 3) {
                        var min = 1
                        try {
                            min = args[2].toInt()
                        } catch (e: NumberFormatException) {
                        }
                        return (min until mobzyConfig.maxCommandSpawns).asIterable()
                            .map { it.toString() }.filter { it.startsWith(min.toString()) }
                    }

                if (subCommand in listOf("remove", "rm", "info", "i"))
                    if (args.size == 2) {
                        val mobs: MutableList<String> = ArrayList()
                        mobs.addAll(MobzyTypesQuery.map { it.key.toString() })
                        mobs.addAll(listOf("all", "npc", "mob", "named", "passive", "hostile", "flying"))
                        return mobs.filter {
                                val arg = args[1].toLowerCase()
                                it.startsWith(arg) || it.substringAfter(":").startsWith(arg)
                            }
                    }
                return if (subCommand == "config") listOf("mobs", "spawns", "domobspawns")
                    .filter { it.toLowerCase().startsWith(args[1].toLowerCase()) }
                else emptyList()
            }
        }
    }
}
