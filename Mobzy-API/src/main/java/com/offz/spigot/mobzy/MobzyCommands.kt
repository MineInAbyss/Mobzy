package com.offz.spigot.mobzy

import com.mineinabyss.idofront.commands.Command.Execution
import com.mineinabyss.idofront.commands.IdofrontCommandExecutor
import com.mineinabyss.idofront.entities.toNMS
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.idofront.messaging.success
import com.offz.spigot.mobzy.gui.MobzyGUI
import com.offz.spigot.mobzy.mobs.types.FlyingMob
import com.offz.spigot.mobzy.mobs.types.HostileMob
import com.offz.spigot.mobzy.mobs.types.PassiveMob
import me.libraryaddict.disguise.utilities.DisguiseUtilities
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MobzyCommands internal constructor(private val context: MobzyContext) : IdofrontCommandExecutor() {
    override val commands = commands(mobzy) {
        command("mobzy", "mz") {
            //the variables are stored for the commands below only
            command("configinfo", "cfginfo") {
                onExecute {
                    val config = mobzy.mobzyConfig
                    sender.info("LOG OF CURRENTLY REGISTERED STUFF:")
                    sender.info(("Mob configs: ${config.mobCfgs}\n" +
                            "Spawn configs: ${config.spawnCfgs}\n" +
                            "Registered addons: ${config.registeredAddons}\n" +
                            "Registered EntityTypes: ${mobzy.customTypes.types}"))
                }
            }
            command("reload", "rl") {
                onExecute {
                    context.mobzyConfig.reload()
                    sender.info("Reloaded config files (not necessarily successfully) :p")
                }
            }
            command("libsdisguisesstats") {
                onExecute {
                    sender.info("${DisguiseUtilities.getDisguises().count()} disguises in use")
                }
            }

            commandGroup {
                val entityType = StringArgument(1, "entity type")
                val radius = IntArgument(2, "radius", default = 0).apply { ensureChangedByPlayer() } //TODO cleaner way of doing this

                fun Execution.removeOrInfo(isInfo: Boolean) {
                    val worlds = mobzy.server.worlds
                    var mobCount = 0
                    var entityCount = 0

                    for (world in worlds) for (entity in world.entities) {
                        val tags = entity.scoreboardTags
                        val nmsEntity = entity.toNMS()
                        if (entity.isCustomMob
                                && (entityType() == "all" && !entity.isRenamed && !entity.scoreboardTags.contains("npc")
                                        || entityType() == "named" && entity.isRenamed
                                        || entityType() == "npc" && entity.scoreboardTags.contains("npc")
                                        || entityType() == "passive" && !entity.scoreboardTags.contains("npc") && nmsEntity is PassiveMob
                                        || entityType() == "hostile" && nmsEntity is HostileMob
                                        || entityType() == "flying" && nmsEntity is FlyingMob
                                        || entity.isOfType(entityType()))) {
                            val playerLoc = (sender as Player).location
                            if (radius() <= 0 || entity.world == playerLoc.world && entity.location.distance(playerLoc) < radius()) {
                                if (!isInfo) entity.remove() //only kill mobs if command was cmrm and not cminfo
                                entityCount++
                                if (!tags.contains("additionalPart")) mobCount++
                            }
                        }
                    }

                    sender.success((if (isInfo) "There are " else "Removed ") +
                            "&l$mobCount&r&a " + (if (entityType() == "all") "custom mobs " else "${entityType()} ") +
                            (if (entityCount != mobCount) "($entityCount entities) " else "") + //account for multi-entity mobs
                            (if (radius() <= 0) "in all loaded chunks." else "in a radius of ${radius()} blocks."), '&'
                    )
                }

                command("remove", "rm") {
                    onExecute { removeOrInfo(false) }
                }

                command("info", "i") {
                    onExecute { removeOrInfo(true) }
                }
            }

            command("spawn", "s") {
                val mobName = StringArgument(1, "mob name") //FIXME the function literal with receiver in onExecute passes null into the getValue method
                var numOfSpawns = IntArgument(2, "number of mobs to spawn", default = 1)

                onlyIfSenderIsPlayer()

                onExecute {
                    if (!mobzy.customTypes.types.containsKey(mobName())) { //TODO a way to add custom checks and errors via the delegation
                        sender.error("No such entity ${mobName()}")
                        return@onExecute
                    }

                    if (numOfSpawns() > MobzyConfig.maxSpawnAmount) numOfSpawns set MobzyConfig.maxSpawnAmount
                    for (i in 0 until numOfSpawns()) (sender as Player).location.spawnEntity(mobName())
                }
            }

            command("list", "l") {
                onExecute {
                    sender.success("All registered types:\n${mobzy.customTypes.types.keys}")
                }
            }
            command("config") {
                command("spawns") {
                    onlyIfSenderIsPlayer()
                    onExecute {
                        //TODO make an onExecutePlayerOnly which casts and registers the condition for us
                        MobzyGUI(sender as Player).show(sender as Player)
                    }
                }
                command("domobspawns") {
                    val enabled = BooleanArgument(1, "enabled")
                    onExecute {
                        MobzyConfig.doMobSpawns = enabled()
                        sender.success("Config option doMobSpawns has been set to ${enabled()}")
                    }
                }
            }
        }
    }

    //TODO make the API do tab completion
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        if (command.name != "mobzy") return emptyList()
        if (args.size <= 1) return listOf("spawn", "info", "remove", "reload", "fullreload", "i", "rm", "s", "config", "libsdisguisesstats")
                .filter { it.startsWith(args[0]) }
        val subCommand = args[0]
        if (subCommand == "spawn" || subCommand == "s")
            if (args.size == 2) {
                return mobzy.customTypes.types.keys
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
                mobs.addAll(mobzy.customTypes.types.keys)
                mobs.addAll(listOf("all", "npc", "mob", "named", "passive", "hostile", "flying"))
                return mobs.filter { it.toLowerCase().startsWith(args[1].toLowerCase()) }
            }
        return if (subCommand == "config") listOf("mobs", "spawns", "domobspawns")
                .filter { it.toLowerCase().startsWith(args[1].toLowerCase()) }
        else emptyList()
    }
}