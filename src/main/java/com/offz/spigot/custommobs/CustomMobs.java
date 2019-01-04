package com.offz.spigot.custommobs;

import com.derongan.minecraft.mineinabyss.AbyssContext;
import com.derongan.minecraft.mineinabyss.MineInAbyss;
import com.offz.spigot.custommobs.Behaviours.Listener.MobListener;
import com.offz.spigot.custommobs.Behaviours.Task.MoveAnimationTask;
import com.offz.spigot.custommobs.Behaviours.WalkingBehaviour;
import com.offz.spigot.custommobs.Loading.MobLoader;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import com.offz.spigot.custommobs.Spawning.SpawnListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CustomMobs extends JavaPlugin {

    private MobContext context;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("removeMobs")) {
            for (Object o : WalkingBehaviour.registeredMobs.keySet()) {
                UUID uuid = (UUID) o;
                Bukkit.getEntity(uuid).remove();
                WalkingBehaviour.unregisterMob(uuid);
            }
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

        if (getConfig().isSet("walkingBehaviour")) {
            Map<UUID, WalkingBehaviour.MobInfo> tempMap = new HashMap<>();
            for (String item : getConfig().getConfigurationSection("walkingBehaviour").getKeys(false)) {
                UUID uuid = UUID.fromString(item);
                Entity e = getServer().getEntity(uuid);
                if (e == null)
                    continue;

                WalkingBehaviour.registerMob(e, MobType.getRegisteredMobType(e), Short.parseShort(getConfig().get("walkingBehaviour." + item).toString()));
            }
        }
    }

    @Override
    public void onDisable() {
        getConfig().set("walkingBehaviour", null);
        for (UUID uuid : WalkingBehaviour.registeredMobs.keySet()) {
            WalkingBehaviour.MobInfo value = WalkingBehaviour.registeredMobs.get(uuid);
            getConfig().set("walkingBehaviour." + uuid.toString(), value.stillDamageValue);
        }
        saveConfig();

        // Plugin shutdown logic
        getLogger().info("onDisable has been invoked!");
    }
}