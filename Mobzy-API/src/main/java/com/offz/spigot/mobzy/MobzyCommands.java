package com.offz.spigot.mobzy;

import com.offz.spigot.mobzy.Mobs.Types.FlyingMob;
import com.offz.spigot.mobzy.Mobs.Types.HostileMob;
import com.offz.spigot.mobzy.Mobs.Types.PassiveMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class MobzyCommands implements org.bukkit.command.CommandExecutor, TabCompleter {
    private MobzyContext context;

    MobzyCommands(MobzyContext context) {
        this.context = context;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("customMobs.reload") && command.getName().equalsIgnoreCase("cmreload")) {
            context.getMobzyConfig().reload();
            sender.sendMessage(ChatColor.GREEN + "Reloaded config files");
            return true;
        }
        List<World> worlds = context.getPlugin().getServer().getWorlds();
        boolean cminfo = command.getName().equalsIgnoreCase("cminfo");
        if (sender.hasPermission("customMobs.remove") && command.getName().equalsIgnoreCase("cmrm") || cminfo) {
            if (args.length < 1) {
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
                            && ((args[0].equalsIgnoreCase("all") && !MobzyAPI.isRenamed(entity) && !entity.getScoreboardTags().contains("npc"))
                            || (args[0].equalsIgnoreCase("named") && MobzyAPI.isRenamed(entity))
                            || (args[0].equalsIgnoreCase("npc") && entity.getScoreboardTags().contains("npc"))
                            || (args[0].equalsIgnoreCase("passive") && !entity.getScoreboardTags().contains("npc") && nmsEntity instanceof PassiveMob)
                            || (args[0].equalsIgnoreCase("hostile") && nmsEntity instanceof HostileMob)
                            || (args[0].equalsIgnoreCase("flying") && nmsEntity instanceof FlyingMob)
                            || MobzyAPI.isMobOfType(entity, args[0])))
                        try {
                            if (args.length < 2 || entity.getLocation().distance(Bukkit.getPlayer(sender.getName()).getLocation()) < Integer.parseInt(args[1])) {
                                if (!cminfo) entity.remove(); //only kill mobs if command was cmrm and not cminfo
                                entityCount++;
                                if (!tags.contains("additionalPart")) mobCount++;
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "Please input entity valid integer as the range");
                            return true;
                        }
                }
            String message = ChatColor.GREEN + "";
            if (cminfo) message += "There are ";
            else message += "Removed ";
            message += ChatColor.BOLD + "" + mobCount + "" + ChatColor.RESET + ChatColor.GREEN;
            if (args[0].equalsIgnoreCase("all"))
                message += " custom mobs ";
            else
                message += " " + args[0] + " ";
            if (entityCount != mobCount)
                message += "(" + entityCount + " entities) ";

            if (args.length < 2) message += "in all loaded chunks";
            else message += " in entity radius of " + args[1] + " blocks";
            sender.sendMessage(message);
            return true;
        }

        if (sender.hasPermission("customMobs.spawn") && command.getName().equalsIgnoreCase("cms")) {
            Player p = Bukkit.getPlayer(sender.getName());
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Enter a mob name");
                return true;
            }

            if (args[0] != null) {
                if (!CustomType.getTypes().containsKey(args[0])) {
                    sender.sendMessage(ChatColor.RED + "No such entity " + args[0]);
                    return true;
                }
                LivingEntity entity = (LivingEntity) CustomType.spawnEntity(args[0], p.getLocation());
            } else
                sender.sendMessage(ChatColor.RED + "Invalid mob name");
            return true;
        }

        if (sender.hasPermission("customMobs.spawn.list") && command.getName().equalsIgnoreCase("cml")) {
            sender.sendMessage(ChatColor.GREEN + CustomType.getTypes().keySet().toString());
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String cmdName = command.getName();
        if (cmdName.equalsIgnoreCase("cms"))
            if (args.length == 1) {
                return CustomType.getTypes().keySet()
                        .stream()
                        .filter(a -> a.startsWith(args[0].toLowerCase()))
                        .collect(toList());
            }
        if (cmdName.equalsIgnoreCase("cmrm") || cmdName.equalsIgnoreCase("cminfo"))
            if (args.length == 1) {
                List<String> mobs = new ArrayList<>();
                mobs.addAll(CustomType.getTypes().keySet());
                mobs.addAll(Arrays.asList("all", "npc", "mob", "named", "passive", "hostile", "flying"));
                return mobs
                        .stream()
                        .filter(a -> a.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(toList());
            }
        return Collections.emptyList();
    }
}
