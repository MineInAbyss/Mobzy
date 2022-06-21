package com.mineinabyss.mobzy

import com.mineinabyss.geary.context.GearyContext
import com.mineinabyss.geary.papermc.GearyMCContextKoin
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.geary.papermc.helpers.spawnFromPrefab
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.commands.arguments.booleanArg
import com.mineinabyss.idofront.commands.arguments.intArg
import com.mineinabyss.idofront.commands.arguments.optionArg
import com.mineinabyss.idofront.commands.arguments.stringArg
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.commands.execution.stopCommand
import com.mineinabyss.idofront.commands.extensions.actions.PlayerAction
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.mobzy.ecs.components.Important
import com.mineinabyss.mobzy.ecs.components.initialization.MobzyType
import com.mineinabyss.mobzy.ecs.components.interaction.Tamed
import com.mineinabyss.mobzy.injection.MobzyTypesQuery
import com.mineinabyss.mobzy.spawning.SpawnRegistry
import com.mineinabyss.mobzy.spawning.SpawnTask
import com.mineinabyss.mobzy.spawning.helpers.categorizeMobs
import net.minecraft.world.entity.FlyingMob
import net.minecraft.world.entity.animal.AbstractFish
import net.minecraft.world.entity.animal.Animal
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Entity
import org.bukkit.entity.Monster
import org.bukkit.entity.NPC

class MobzyCommands : IdofrontCommandExecutor(), TabCompleter, GearyContext by GearyMCContextKoin() {
    override val commands = commands(mobzy) {
        ("mobzy" / "mz") {
            ("reload" / "rl")(desc = "Reloads the configuration files") {
                "spawns" {
                    SpawnRegistry.reloadSpawns()
                    sender.success("Reloaded spawn config")
                }

                action {
                    MobzyConfig.reload(sender)
                }
            }

            commandGroup {
                val query by stringArg()
                val radius by intArg { default = 0 }

                fun PlayerAction.removeOrInfo(isInfo: Boolean) {
                    val worlds = mobzy.server.worlds
                    var entityCount = 0
                    val entities = mutableSetOf<Entity>()
                    val types = query.split("+")

                    for (world in worlds) for (entity in world.entities) {
                        val nmsEntity = entity.toNMS()
                        val geary = entity.toGeary()
                        if (!geary.has<MobzyType>()) continue


                        if (types.any { type ->
                                fun excludeDefault() = !geary.has<Important>() && entity.customName() == null && !geary.has<Tamed>()
                                when (type) {
                                    "custom" -> excludeDefault()
                                    "passive" -> nmsEntity is Animal && excludeDefault()
                                    "hostile" -> nmsEntity is Monster && excludeDefault()
                                    "renamed" -> entity.customName() != null && nmsEntity !is NPC
                                    "tamed" -> geary.has<Tamed>()
                                    "important" -> geary.has<Important>() && entity.customName() == null
                                    "flying" -> nmsEntity is FlyingMob && excludeDefault()
                                    "fish" -> nmsEntity is AbstractFish && excludeDefault()
                                    else -> {
                                        val prefab = runCatching { PrefabKey.of(type).toEntity() }.getOrNull()
                                            ?: this@commandGroup.stopCommand("No such prefab or selector $type")
                                        geary.instanceOf(prefab)
                                    }
                                }
                            }) {
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
                        <b>$entityCount</b> entities matching your query
                        ${if (radius <= 0) "in all loaded chunks." else "in a radius of $radius blocks."}
                        """.trimIndent().replace("\n", " ")
                    )
                    if (isInfo) {
                        val categories = entities.categorizeMobs().entries.sortedByDescending { it.value }
                        if (categories.isNotEmpty()) sender.info(
                            categories.joinToString("\n") { (type, amount) -> "<gray>${type}</gray>: $amount" }
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
                val mobKey by optionArg(options = MobzyTypesQuery.run { map { it.key.toString() } }) {
                    parseErrorMessage = { "No such entity: $passed" }
                }
                val numOfSpawns by intArg {
                    name = "number of spawns"
                    default = 1
                }

                playerAction {
                    val cappedSpawns = numOfSpawns.coerceAtMost(mobzyConfig.maxCommandSpawns)
                    val key = PrefabKey.of(mobKey)

                    repeat(cappedSpawns) {
                        player.location.spawnFromPrefab(key) ?: error("Error while spawning $mobKey")
                    }
                }
            }

            "debug" {
                createDebugCommands()
            }

            ("list" / "l")(desc = "Lists all custom mob types")?.action {
                sender.success("All registered types:\n${MobzyTypesQuery.getKeys()}")
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

    private val mobs: List<String> by lazy {
        buildList {
            addAll(listOf("custom", "important", "mob", "renamed", "passive", "hostile", "flying"))
            addAll(MobzyTypesQuery.getKeys().map { it.toString() })
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
                        return MobzyTypesQuery.run {
                            filter {
                                val arg = args[1].lowercase()
                                it.key.key.startsWith(arg) || it.key.full.startsWith(arg)
                            }.map { it.key.toString() }
                        }
                    } else if (args.size == 3) {
                        var min = 1
                        try {
                            min = args[2].toInt()
                        } catch (_: NumberFormatException) {
                        }
                        return (min until mobzyConfig.maxCommandSpawns).asIterable()
                            .map { it.toString() }.filter { it.startsWith(min.toString()) }
                    }

                if (subCommand in listOf("remove", "rm", "info", "i"))
                    if (args.size == 2) {
                        val query = args[1].lowercase()
                        val parts = query.split("+")
                        val withoutLast = query.substringBeforeLast("+", missingDelimiterValue = "").let {
                            if (parts.size > 1) "$it+" else it
                        }
                        return mobs.asSequence().filter {
                            it !in parts && (it.startsWith(parts.last()) ||
                                    it.substringAfter(":").startsWith(parts.last()))
                        }.take(20).map { "$withoutLast$it" }.toList()
                    }
                return if (subCommand == "config") listOf("mobs", "spawns", "domobspawns")
                    .filter { it.lowercase().startsWith(args[1].lowercase()) }
                else emptyList()
            }
        }
    }
}
