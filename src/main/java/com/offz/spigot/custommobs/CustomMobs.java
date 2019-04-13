package com.offz.spigot.custommobs;

import com.derongan.minecraft.mineinabyss.AbyssContext;
import com.derongan.minecraft.mineinabyss.MineInAbyss;
import com.offz.spigot.custommobs.Behaviours.AnimationBehaviour;
import com.offz.spigot.custommobs.Behaviours.Listener.MobListener;
import com.offz.spigot.custommobs.Behaviours.Task.MoveAnimationTask;
import com.offz.spigot.custommobs.Loading.MobLoader;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import com.offz.spigot.custommobs.Spawning.SpawnListener;
import com.offz.spigot.custommobs.Spawning.SpawnTask;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomMobs extends JavaPlugin {

    private MobContext context;

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
        getServer().getScheduler().scheduleSyncRepeatingTask(this, moveAnimationTask, 0, 5);
        Runnable spawnTask = new SpawnTask(abyssContext);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, spawnTask, 0, 100);

        CMCommandExecutor commandExecutor = new CMCommandExecutor(context);

        this.getCommand("cmrm").setExecutor(commandExecutor);
        this.getCommand("cminfo").setExecutor(commandExecutor);
        this.getCommand("cms").setExecutor(commandExecutor);
        this.getCommand("cml").setExecutor(commandExecutor);

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
        getLogger().info(ChatColor.GREEN + "CustomMobs: Loaded " + num + " custom entities on startup");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("onDisable has been invoked!");
    }
}