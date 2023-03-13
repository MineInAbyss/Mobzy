package com.mineinabyss.mobzy

import com.mineinabyss.geary.papermc.tracking.entities.components.SetEntityType
import com.mineinabyss.geary.papermc.tracking.entities.entityTracking
import com.mineinabyss.geary.papermc.tracking.entities.helpers.spawnFromPrefab
import com.mineinabyss.geary.papermc.tracking.entities.toGeary
import com.mineinabyss.geary.prefabs.PrefabKey
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
import com.mineinabyss.mobzy.features.spawning.Important
import com.mineinabyss.mobzy.features.taming.Tamed
import com.mineinabyss.mobzy.spawning.mobzySpawning
import com.mineinabyss.mobzy.spawning.vertical.SpawnInfo
import net.minecraft.world.entity.FlyingMob
import net.minecraft.world.entity.animal.AbstractFish
import net.minecraft.world.entity.animal.Animal
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Entity
import org.bukkit.entity.Monster
import org.bukkit.entity.NPC

class MobzyCommands : IdofrontCommandExecutor(), TabCompleter {
    override val commands = commands(mobzy.plugin) {
        ("mobzy" / "mz") {
            ("reload" / "rl")(desc = "Reloads the configuration files") {
                "spawns" {
                    mobzySpawning.spawnRegistry.reloadSpawns()
                    sender.success("Reloaded spawn config")
                }

                //TODO proper config reload support
            }

            commandGroup {
                val query by stringArg()
                val radius by intArg { default = 0 }

                fun PlayerAction.removeOrInfo(isInfo: Boolean) {
                    val worlds = mobzy.plugin.server.worlds
                    var entityCount = 0
                    val entities = mutableSetOf<Entity>()
                    val types = query.split("+")

                    for (world in worlds) for (entity in world.entities) {
                        val nmsEntity = entity.toNMS()
                        val geary = entity.toGeary()
                        if (!geary.has<SetEntityType>()) continue


                        if (types.any { type ->
                                fun excludeDefault() =
                                    !geary.has<Important>() && entity.customName() == null && !geary.has<Tamed>()
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
                                        val prefab = runCatching { PrefabKey.of(type).toEntityOrNull() }.getOrNull()
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
                        val categories = SpawnInfo.categorizeByType(entities).entries.sortedByDescending { it.value }
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
                val mobKey by optionArg(options = entityTracking.mobPrefabs.run { map { it.key.toString() } }) {
                    parseErrorMessage = { "No such entity: $passed" }
                }
                val numOfSpawns by intArg {
                    name = "number of spawns"
                    default = 1
                }

                playerAction {
                    val cappedSpawns = numOfSpawns.coerceAtMost(mobzySpawning.config.maxCommandSpawns)
                    val key = PrefabKey.of(mobKey)

                    repeat(cappedSpawns) {
                        player.location.spawnFromPrefab(key) ?: error("Error while spawning $mobKey")
                    }
                }
            }

            "debug" {
                createDebugCommands()
            }

            "locate" {
                val mobKey by optionArg(options = entityTracking.mobPrefabs.run { map { it.key.toString() } }) {
                    parseErrorMessage = { "No such entity: $passed" }
                }
                val radius by intArg {
                    name = "radius to check"
                    default = 0
                }
                playerAction {
                    val key = PrefabKey.of(mobKey)
                    if (radius <= 0) {
                        Bukkit.getWorlds().forEach { world ->
                            world.entities.filter { it.toGeary().instanceOf(key.toEntity()) }.forEach { entity ->
                                val loc = entity.location
                                player.info("Found ${key.key} at <click:run_command:/teleport ${loc.blockX} ${loc.blockY} ${loc.blockZ}>${entity.location}>${entity.location} in ${entity.world.name}")
                            }
                        }
                    } else {
                        player.location.getNearbyEntities(radius.toDouble(), radius.toDouble(), radius.toDouble())
                            .filter { it.toGeary().instanceOf(key.toEntity()) }.forEach { entity ->
                                val loc = entity.location
                                player.info("Found ${key.key} at <click:run_command:/teleport ${loc.blockX} ${loc.blockY} ${loc.blockZ}>${entity.location}")

                            }
                    }
                }
            }

            ("list" / "l")(desc = "Lists all custom mob types")?.action {
                sender.success("All registered types:\n${entityTracking.mobPrefabs.getKeys()}")
            }
        }
    }

    private val mobs: List<String> by lazy {
        buildList {
            addAll(listOf("custom", "important", "mob", "renamed", "passive", "hostile", "flying"))
            addAll(entityTracking.mobPrefabs.getKeys().map { it.toString() })
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
                "stats"
            ).filter { it.startsWith(args[0]) }

            else -> {
                val subCommand = args[0]

                when (subCommand) {
                    "spawn", "s" -> if (args.size == 2) {
                        return entityTracking.mobPrefabs.run {
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
                        return (min until mobzySpawning.config.maxCommandSpawns).asIterable()
                            .map { it.toString() }.filter { it.startsWith(min.toString()) }
                    }

                    "remove", "rm", "info", "i" -> if (args.size == 2) {
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
                }
                return emptyList()
            }
        }
    }
}
