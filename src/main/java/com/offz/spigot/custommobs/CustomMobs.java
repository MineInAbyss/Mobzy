package com.offz.spigot.custommobs;

import com.derongan.minecraft.mineinabyss.AbyssContext;
import com.derongan.minecraft.mineinabyss.MineInAbyss;
import com.offz.spigot.custommobs.Listener.MobListener;
import com.offz.spigot.custommobs.Loading.CustomType;
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
//        getServer().getPluginManager().registerEvents(new SpawnListener(abyssContext), this);

        //Register repeating tasks
        /*Runnable mobTask = new MobTask();
        getServer().getScheduler().scheduleSyncRepeatingTask(this, mobTask, 0, 1);
        Runnable moveAnimationTask = new MoveAnimationTask();
        getServer().getScheduler().scheduleSyncRepeatingTask(this, moveAnimationTask, 0, 5);*/
//        Runnable spawnTask = new SpawnTask(abyssContext);
//        getServer().getScheduler().scheduleSyncRepeatingTask(this, spawnTask, 0, 100);

        CMCommandExecutor commandExecutor = new CMCommandExecutor(context);

        this.getCommand("cmrm").setExecutor(commandExecutor);
        this.getCommand("cminfo").setExecutor(commandExecutor);
        this.getCommand("cms").setExecutor(commandExecutor);
        this.getCommand("cml").setExecutor(commandExecutor);

        CustomType.registerAllMobs();
//        MobLoader.loadAllMobs(context);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("onDisable has been invoked!");
    }
}