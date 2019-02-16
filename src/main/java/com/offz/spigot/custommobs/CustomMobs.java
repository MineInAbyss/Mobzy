package com.offz.spigot.custommobs;

import com.derongan.minecraft.mineinabyss.AbyssContext;
import com.derongan.minecraft.mineinabyss.MineInAbyss;
import com.offz.spigot.custommobs.Behaviours.AnimationBehaviour;
import com.offz.spigot.custommobs.Behaviours.Listener.MobListener;
import com.offz.spigot.custommobs.Behaviours.Task.MoveAnimationTask;
import com.offz.spigot.custommobs.Loading.CustomType;
import com.offz.spigot.custommobs.Loading.MobLoader;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import com.offz.spigot.custommobs.Spawning.SpawnListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class CustomMobs extends JavaPlugin {

    private MobContext context;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("customMobs.remove") && label.equalsIgnoreCase("cmrm")) {
            int num = 0;
            for (World world : getServer().getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    if (entity.getScoreboardTags().contains("customMob")) {
                        UUID uuid = entity.getUniqueId();
                        entity.remove();
                        AnimationBehaviour.unregisterMob(uuid);
                        num++;
                    }
                }
            }
            sender.sendMessage(ChatColor.GREEN + "CustomMobs: Removed " + num + " custom entities (around " + num / 3 + " mobs) from all worlds");
            return true;
        } else if (sender.isOp() && label.equalsIgnoreCase("cminfo")) {
            int num = 0;
            for (World world : getServer().getWorlds())
                for (Entity entity : world.getEntities())
                    if (entity.getScoreboardTags().contains("customMob"))
                        num++;
            sender.sendMessage(ChatColor.GREEN + "CustomMobs: There are " + num + " custom entities (around " + num / 3 + " mobs) in all worlds");
            return true;
        }

        if (sender.hasPermission("customMobs.spawn") && label.equalsIgnoreCase("cms")) {
            if (SpawnListener.spawnEntity(args[0], Bukkit.getPlayer(sender.getName()).getLocation()))
                sender.sendMessage(ChatColor.GREEN + "Spawned " + args[0]);
            else
                sender.sendMessage(ChatColor.RED + "Invalid mob name");
            return true;
        }

        if (sender.hasPermission("customMobs.spawn.list") && label.equalsIgnoreCase("cml")) {
//            for(String e: )
            sender.sendMessage(ChatColor.GREEN + CustomType.types.keySet().toString());
            return true;
        }
        return false;
    }

    @Override
    public void onEnable() {
        AbyssContext abyssContext = MineInAbyss.getContext();
        // Plugin startup logic
        context = new MobContext(getConfig()); //Create new context and add plugin and logger to it
        context.setPlugin(this);
        context.setLogger(getLogger());

        getLogger().info("On enable has been called");

        //Register events
        getServer().getPluginManager().registerEvents(new MobListener(context), this);
        getServer().getPluginManager().registerEvents(new SpawnListener(abyssContext), this);

        //Register repeating tasks
        Runnable mobTask = new MobTask();
        getServer().getScheduler().scheduleSyncRepeatingTask(this, mobTask, 0, 1);
        Runnable moveAnimationTask = new MoveAnimationTask();
        getServer().getScheduler().scheduleSyncRepeatingTask(this, moveAnimationTask, 0, 1);

        MobLoader.loadAllMobs(context);

        int num = 0;
        for (World world : getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getScoreboardTags().contains("customMob")) {
                        MobType type = MobType.getRegisteredMobType(entity);
                        if (type != null && type.getBehaviour() instanceof AnimationBehaviour && !AnimationBehaviour.registeredMobs.containsKey(entity.getUniqueId())) {
                            AnimationBehaviour.registerMob(entity, type, type.getModelID());
                        }
                    num++;
                }
            }
        }
        getLogger().info(ChatColor.GREEN + "CustomMobs: Loaded " + num + " custom entities (around " + num / 3 + " mobs) from all worlds");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("onDisable has been invoked!");
    }
}