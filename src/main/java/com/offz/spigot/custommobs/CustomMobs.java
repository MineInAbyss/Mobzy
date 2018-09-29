package com.offz.spigot.custommobs;

import com.offz.spigot.custommobs.Behaviours.MobHitListener;
import com.offz.spigot.custommobs.Loading.MobLoader;
import com.offz.spigot.custommobs.Spawning.SpawnListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomMobs extends JavaPlugin {
    private MobContext context;

    @Override
    public void onEnable() {
        // Plugin startup logic

        context = new MobContext(getConfig()); //Create new context and add plugin and logger to it
        context.setPlugin(this);
        context.setLogger(getLogger());

        getLogger().info("On enable has been called");


        getServer().getPluginManager().registerEvents(new SpawnListener(), this);
        getServer().getPluginManager().registerEvents(new MobHitListener(context), this);

        MobLoader.loadAllMobs(context);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("onDisable has been invoked!");

        Runnable staminaTask = new MobTask();
        getServer().getScheduler().scheduleSyncRepeatingTask(this, staminaTask, 0, 1);
    }
}
