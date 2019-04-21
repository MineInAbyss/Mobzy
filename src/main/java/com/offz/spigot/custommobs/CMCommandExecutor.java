package com.offz.spigot.custommobs;

import com.offz.spigot.custommobs.Behaviours.AnimationBehaviour;
import com.offz.spigot.custommobs.Loading.CustomType;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import net.minecraft.server.v1_13_R2.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class CMCommandExecutor implements CommandExecutor, TabCompleter {
    private MobContext context;

    public CMCommandExecutor(MobContext context) {
        this.context = context;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<World> worlds = context.getPlugin().getServer().getWorlds();
        boolean cminfo = label.equalsIgnoreCase("cminfo");
        if (sender.hasPermission("customMobs.remove") && label.equalsIgnoreCase("cmrm") || cminfo) {
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Please specify entity mob type");
                return true;
            }
            int mobCount = 0;
            int entityCount = 0;
            for (World world : worlds) {
                for (Entity entity : world.getEntities()) {
                    Set<String> tags = entity.getScoreboardTags();
                    if (tags.contains("customMob")
                            && args[0].equalsIgnoreCase("all") || tags.contains(MobType.toEntityTypeID(args[0]).toLowerCase())) {
                        try {
                            if (args.length < 2 || entity.getLocation().distance(Bukkit.getPlayer(sender.getName()).getLocation()) < Integer.parseInt(args[1])) {
                                if (!cminfo) { //only kill mobs if command was cmrm and not cminfo
                                    UUID uuid = entity.getUniqueId();
                                    entity.remove();
                                    AnimationBehaviour.unregisterMob(uuid);
                                }
                                entityCount++;
                                if (!tags.contains("additionalPart"))
                                    mobCount++;
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "Please input entity valid integer as the range");
                            return true;
                        }
                    }
                }
            }
            String message = "";
            if (cminfo)
                message += "There are ";
            else
                message += "Removed ";
            message += mobCount + " custom mobs, composed of " + entityCount + " entities";

            if (args.length < 2)
                message += " in all loaded chunks";
            else
                message += " in entity radius of " + args[1] + " blocks";
            sender.sendMessage(ChatColor.GREEN + message);
            return true;
        }

        if (sender.hasPermission("customMobs.spawn") && label.equalsIgnoreCase("cms")) {
            Player p = Bukkit.getPlayer(sender.getName());
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Enter a mob name");
                return true;
            }

            MobType type = MobType.getRegisteredMobType(args[0]);
            if (type != null) {
                Class entityClass = type.getEntityClass();
                try {
                    Bukkit.broadcastMessage(type.getName());
                    EntityLiving entity = (EntityLiving) entityClass.getConstructor(net.minecraft.server.v1_13_R2.World.class).newInstance(((CraftWorld) p.getWorld()).getHandle());
                    Location loc = p.getLocation();
                    entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), 0);
                    ((CraftWorld) p.getWorld()).getHandle().addEntity(entity);
                    sender.sendMessage(ChatColor.GREEN + "Spawned " + args[0]);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
//            SpawnListener.spawnEntity(args[0], Bukkit.getPlayer(sender.getName()).getLocation());
//            if (SpawnTask.spawnMultiple(args[0], Bukkit.getPlayer(sender.getName()).getLocation(), Integer.parseInt(args[1]), Integer.parseInt(args[2]))) //spawn multiple
//                sender.sendMessage(ChatColor.GREEN + "Spawned " + args[0]);
            else
                sender.sendMessage(ChatColor.RED + "Invalid mob name");
            return true;
        }

        if (sender.hasPermission("customMobs.spawn.list") && label.equalsIgnoreCase("cml")) {
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
                mobs.addAll(Arrays.asList("ALL", "NPC", "MOB"));
                return mobs
                        .stream()
                        .filter(a -> a.startsWith(args[0].toLowerCase()))
                        .collect(toList());
            }
        return Collections.emptyList();
    }
}
