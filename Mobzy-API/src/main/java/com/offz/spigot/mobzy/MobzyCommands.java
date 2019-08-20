package com.offz.spigot.mobzy;

import com.offz.spigot.mobzy.GUI.MobzyGUI;
import com.offz.spigot.mobzy.Mobs.Types.FlyingMob;
import com.offz.spigot.mobzy.Mobs.Types.HostileMob;
import com.offz.spigot.mobzy.Mobs.Types.PassiveMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class MobzyCommands implements org.bukkit.command.CommandExecutor, TabCompleter {
    private MobzyContext context;

    MobzyCommands(MobzyContext context) {
        this.context = context;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("mobzy") || args.length < 1)
            return false;

        String subCommand = args[0];

        if (sender.hasPermission("mobzy.cfginfo") && subCommand.equalsIgnoreCase("cfginfo")) {
            sender.sendMessage(ChatColor.GREEN + "LOG OF CURRENTLY REGISTERED STUFF:");
            Mobzy plugin = JavaPlugin.getPlugin(Mobzy.class);
            MobzyConfig config = plugin.getMobzyConfig();
            sender.sendMessage("Mob configs: " + config.getMobCfgs());
            sender.sendMessage("Spawn configs: " + config.getSpawnCfgs());
            sender.sendMessage("Registered addons: " + config.getRegisteredAddons());
            sender.sendMessage("Registered EntityTypes: " + CustomType.getTypes());
        }

        if (sender.hasPermission("mobzy.reload") && subCommand.equalsIgnoreCase("reload")) {
            context.getMobzyConfig().reload();
            sender.sendMessage("Reloaded config files (not necessarily successfully) :p");
            return true;
        }
        List<World> worlds = context.getPlugin().getServer().getWorlds();
        boolean info = subCommand.equalsIgnoreCase("info") || subCommand.equalsIgnoreCase("i");
        if (sender.hasPermission("mobzy.remove") && (subCommand.equalsIgnoreCase("remove") || subCommand.equalsIgnoreCase("rm")) || info) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Please specify entity mob type");
                return true;
            }
            int mobCount = 0;
            int entityCount = 0;
            for (World world : worlds)
                for (Entity entity : world.getEntities()) {
                    Set<String> tags = entity.getScoreboardTags();
                    net.minecraft.server.v1_13_R2.Entity nmsEntity = MobzyAPI.toNMS(entity);
                    if (MobzyAPI.isCustomMob(entity)
                            && ((args[1].equalsIgnoreCase("all") && !MobzyAPI.isRenamed(entity) && !entity.getScoreboardTags().contains("npc"))
                            || (args[1].equalsIgnoreCase("named") && MobzyAPI.isRenamed(entity))
                            || (args[1].equalsIgnoreCase("npc") && entity.getScoreboardTags().contains("npc"))
                            || (args[1].equalsIgnoreCase("passive") && !entity.getScoreboardTags().contains("npc") && nmsEntity instanceof PassiveMob)
                            || (args[1].equalsIgnoreCase("hostile") && nmsEntity instanceof HostileMob)
                            || (args[1].equalsIgnoreCase("flying") && nmsEntity instanceof FlyingMob)
                            || MobzyAPI.isMobOfType(entity, args[1])))
                        try {
                            Location playerLoc = Bukkit.getPlayer(sender.getName()).getLocation();
                            if (args.length < 3 || entity.getWorld().equals(playerLoc.getWorld()) && entity.getLocation().distance(playerLoc) < Integer.parseInt(args[2])) {
                                if (!info) entity.remove(); //only kill mobs if command was cmrm and not cminfo
                                entityCount++;
                                if (!tags.contains("additionalPart")) mobCount++;
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "Please input entity valid integer as the range");
                            return true;
                        }
                }
            String message = ChatColor.GREEN + "";
            if (info) message += "There are ";
            else message += "Removed ";
            message += ChatColor.BOLD + "" + mobCount + "" + ChatColor.RESET + ChatColor.GREEN;
            if (args[1].equalsIgnoreCase("all"))
                message += " custom mobs ";
            else
                message += " " + args[1] + " ";
            if (entityCount != mobCount)
                message += "(" + entityCount + " entities) ";

            if (args.length < 3) message += "in all loaded chunks";
            else message += " in entity radius of " + args[1] + " blocks";
            sender.sendMessage(message);
            return true;
        }

        if (sender.hasPermission("mobzy.spawn") && (subCommand.equalsIgnoreCase("spawn") || subCommand.equalsIgnoreCase("s"))) {
            Player p = Bukkit.getPlayer(sender.getName());
            if (args.length == 1) {
                sender.sendMessage(ChatColor.RED + "Enter a mob name");
                return true;
            }

            if (args[1] != null) {
                if (!CustomType.getTypes().containsKey(args[1])) {
                    sender.sendMessage(ChatColor.RED + "No such entity " + args[1]);
                    return true;
                }
                int numOfSpawns = 1;
                try {
                    if (args.length == 3 && args[2] != null)
                        numOfSpawns = Integer.parseInt(args[2]);

                    if (numOfSpawns > MobzyConfig.getMaxSpawnAmount())
                        numOfSpawns = MobzyConfig.getMaxSpawnAmount();

                    for (int i = 0; i < numOfSpawns; i++)
                        CustomType.spawnEntity(args[1], p.getLocation());
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number");
                }

            } else
                sender.sendMessage(ChatColor.RED + "Invalid mob name");
            return true;
        }

        if (sender.hasPermission("mobzy.spawn.list") && (subCommand.equalsIgnoreCase("list") || subCommand.equalsIgnoreCase("l"))) {
            sender.sendMessage(ChatColor.GREEN + CustomType.getTypes().keySet().toString());
            return true;
        }

        if (sender.hasPermission("mobzy.config") && (subCommand.equalsIgnoreCase("config"))) {
            if (args.length == 1) {
                sender.sendMessage(ChatColor.RED + "Enter a config option");
                return true;
            }
            if (args[1].equals("spawns")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    new MobzyGUI(player, JavaPlugin.getPlugin(Mobzy.class)).show(player);
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("mobzy"))
            return Collections.emptyList();

        if (args.length <= 1)
            return Stream.of("spawn", "info", "remove", "reload", "i", "rm", "s", "config")
                    .filter(a -> a.startsWith(args[0]))
                    .collect(toList());

        String subCommand = args[0];
        String cmdName = command.getName();
        if (subCommand.equalsIgnoreCase("spawn") || subCommand.equalsIgnoreCase("s"))
            if (args.length == 2) {
                return CustomType.getTypes().keySet().stream()
                        .filter(a -> a.startsWith(args[1].toLowerCase()))
                        .collect(toList());
            } else if (args.length == 3) {
                int min = 1;

                try {
                    min = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                }
                return IntStream.range(min, MobzyConfig.getMaxSpawnAmount() + 1)
                        .boxed()
                        .map(Object::toString)
                        .collect(toList());
            }
        if (subCommand.equalsIgnoreCase("remove") || subCommand.equalsIgnoreCase("rm") || subCommand.equalsIgnoreCase("info") || subCommand.equalsIgnoreCase("i"))
            if (args.length == 2) {
                List<String> mobs = new ArrayList<>();
                mobs.addAll(CustomType.getTypes().keySet());
                mobs.addAll(Arrays.asList("all", "npc", "mob", "named", "passive", "hostile", "flying"));
                return mobs.stream()
                        .filter(a -> a.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(toList());
            }
        if (subCommand.equalsIgnoreCase("config"))
            return Stream.of("mobs", "spawns")
                    .filter(a -> a.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(toList());

        return Collections.emptyList();
    }
}
