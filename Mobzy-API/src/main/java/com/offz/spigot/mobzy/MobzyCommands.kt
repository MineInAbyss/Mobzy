package com.offz.spigot.mobzy

import com.offz.spigot.mobzy.gui.MobzyGUI
//import com.offz.spigot.mobzy.mobs.types.FlyingMob
//import com.offz.spigot.mobzy.mobs.types.HostileMob
import com.offz.spigot.mobzy.mobs.types.PassiveMob
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class MobzyCommands internal constructor(private val context: MobzyContext) : CommandExecutor, TabCompleter {
    private val errorColor = ChatColor.RED
    private val successColor = ChatColor.GREEN

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name != "mobzy" || args.isEmpty()) return false
        val subCommand = args[0]

        //TODO I'm just having fun with this right now, I'll make a better system that registers the subcommand or use
        // an api for it if I can find one
        fun String.permitted(vararg sub: String) = sender.hasPermission(this) && sub.contains(subCommand)

        fun sendError(message: String) = sender.sendMessage("$errorColor$message")
        fun sendSuccess(message: String) = sender.sendMessage("$successColor$message")
        fun sendInfo(message: String) = sender.sendMessage(message)

        //cfginfo
        if ("mobzy.cfginfo".permitted("cfginfo")) {
            sendSuccess("LOG OF CURRENTLY REGISTERED STUFF:")
            val config = Mobzy.getInstance().mobzyConfig
            sendInfo("Mob configs: ${config.mobCfgs}\n" +
                    "Spawn configs: ${config.spawnCfgs}\n" +
                    "Registered addons: ${config.registeredAddons}\n" +
                    "Registered EntityTypes: ${CustomType.types}")
        }

        //reload
        else if ("mobzy.reload".permitted("reload")) {
            context.mobzyConfig.reload()
            sendInfo("Reloaded config files (not necessarily successfully) :p")
            return true
        }
        //reload
        else if ("mobzy.reload".permitted("fullreload")) { //FIXME running this after the class has been modified results in a NoClassDefFoundError being thrown
            sendInfo("Reloaded the plugin and its components")
            if (!Bukkit.getPluginManager().isPluginEnabled("Plugman")) {
                sendError("Plugman needs to be enabled for this command to work"); return true
            }
            sendSuccess("The plugin and its components have been reloaded")
            Bukkit.getScheduler().scheduleSyncDelayedTask(Mobzy.getInstance()) {
                val map = context.mobzyConfig.registeredAddons.map { (it as JavaPlugin).name }

                Bukkit.getServer().dispatchCommand(Bukkit.getServer().consoleSender, "plugman unload Mobzy")
                map.forEach { Bukkit.getServer().dispatchCommand(Bukkit.getServer().consoleSender, "plugman unload $it") }

                Bukkit.getServer().dispatchCommand(Bukkit.getServer().consoleSender, "plugman load Mobzy")
                map.forEach { Bukkit.getServer().dispatchCommand(Bukkit.getServer().consoleSender, "plugman load $it") }
            }
            return true
        }

        //remove or info
        val worlds = Mobzy.getInstance().server.worlds
        val info = "mobzy.info".permitted("info", "i")
        if (info || "mobzy.remove".permitted("remove", "rm")) {
            if (sender !is Player) {
                sendError("Command can only be used by a player"); return true
            }

            if (args.size < 2) {
                sendError("Please specify entity mob type")
                return true
            }
            var mobCount = 0
            var entityCount = 0
            for (world in worlds) for (entity in world.entities) {
                val tags = entity.scoreboardTags
                val nmsEntity = entity.toNMS()
                if (entity.isCustomMob
                        && (args[1] == "all" && !entity.isRenamed && !entity.scoreboardTags.contains("npc")
                                || args[1] == "named" && entity.isRenamed
                                || args[1] == "npc" && entity.scoreboardTags.contains("npc")
                                || args[1] == "passive" && !entity.scoreboardTags.contains("npc") && nmsEntity is PassiveMob
//                                || args[1] == "hostile" && nmsEntity is HostileMob //FIXME
//                                || args[1] == "flying" && nmsEntity is FlyingMob
                                || entity.isOfType(args[1])))
                    try {
                        val playerLoc = sender.location
                        if (args.size < 3 || entity.world == playerLoc.world && entity.location.distance(playerLoc) < args[2].toInt()) {
                            if (!info) entity.remove() //only kill mobs if command was cmrm and not cminfo
                            entityCount++
                            if (!tags.contains("additionalPart")) mobCount++
                        }
                    } catch (e: NumberFormatException) {
                        sendError("Please input entity valid integer as the range")
                        return true
                    }
            }

            sendSuccess((if (info) "There are" else "Removed") +
                    "${ChatColor.BOLD} $mobCount${ChatColor.RESET}$successColor " + //bold mob count
                    (if (args[1].equals("all", true)) "custom mobs " else "${args[1]} ") + //the name of the mob type removed
                    (if (entityCount != mobCount) "($entityCount entities) " else "") + //account for multi-entity mobs
                    (if (args.size < 3) "in all loaded chunks." else "in a radius of ${args[2]} blocks.")) //everywhere or in a radius
            return true
        }

        //spawn
        else if ("mobzy.spawn".permitted("spawn", "s")) {
            if (sender !is Player) {
                sendError("Command can only be used by a player"); return true
            } else if (args.size == 1) {
                sendError("Enter a mob name"); return true
            } else if (!CustomType.types.containsKey(args[1])) {
                sendError("No such entity ${args[1]}"); return true
            }

            var numOfSpawns = 1
            if (args.size == 3) numOfSpawns = try {
                args[2].toInt()
            } catch (e: NumberFormatException) {
                sendError("${args[2]} is not a valid number")
                return true
            }

            if (numOfSpawns > MobzyConfig.getMaxSpawnAmount()) numOfSpawns = MobzyConfig.getMaxSpawnAmount()
            for (i in 0 until numOfSpawns) CustomType.spawnEntity(args[1], sender.location)
            return true
        }

        //list
        else if ("mobzy.spawn.list".permitted("list", "l")) {
            sendSuccess(CustomType.types.keys.toString())
            return true
        }

        //config
        else if ("mobzy.config".permitted("config")) {
            when {
                sender !is Player -> sendError("Command can only be used by a player")
                args.size == 1 -> sendError("Enter a config option")
                args[1] == "spawns" -> MobzyGUI(sender).show(sender)
            }
            return true
        }
        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        if (command.name != "mobzy") return emptyList()
        if (args.size <= 1) return listOf("spawn", "info", "remove", "reload", "fullreload", "i", "rm", "s", "config")
                .filter { a: String -> a.startsWith(args[0]) }
        val subCommand = args[0]
        if (subCommand == "spawn" || subCommand == "s")
            if (args.size == 2) {
                return CustomType.types.keys
                        .filter { a: String -> a.startsWith(args[1].toLowerCase()) }
            } else if (args.size == 3) {
                var min = 1
                try {
                    min = args[2].toInt()
                } catch (e: NumberFormatException) {
                }
                return (min until MobzyConfig.getMaxSpawnAmount()).asIterable()
                        .map { it.toString() }.filter { it.startsWith(min.toString()) }
            }
        if (subCommand in listOf("remove", "rm", "info", "i"))
            if (args.size == 2) {
                val mobs: MutableList<String> = ArrayList()
                mobs.addAll(CustomType.types.keys)
                mobs.addAll(listOf("all", "npc", "mob", "named", "passive", "hostile", "flying"))
                return mobs.filter { it.toLowerCase().startsWith(args[1].toLowerCase()) }
            }
        return if (subCommand == "config") listOf("mobs", "spawns")
                .filter { it.toLowerCase().startsWith(args[1].toLowerCase()) }
        else emptyList()
    }

}