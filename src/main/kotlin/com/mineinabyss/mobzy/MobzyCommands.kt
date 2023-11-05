package com.mineinabyss.mobzy

import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.mobzy.spawning.mobzySpawning
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

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

            "debug" {
                createDebugCommands()
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
                "reload",
                "debug"
            ).filter { it.startsWith(args[0]) }

            else -> emptyList()
        }
    }
}
