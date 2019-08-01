package com.offz.spigot.mobzy;

import com.derongan.minecraft.guiy.GuiListener;
import com.offz.spigot.mobzy.Listener.MobListener;
import com.offz.spigot.mobzy.Mobs.CustomMob;
import com.offz.spigot.mobzy.Spawning.SpawnTask;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public final class Mobzy extends JavaPlugin {
    public static StringFlag CM_SPAWN_REGIONS;
    private MobzyConfig mobzyConfig;
    private MobzyContext context;

    @Override
    public void onLoad() {
        //TODO try to allow plugin spawning in WorldGuard's config automatically

        //Registering custom WorldGuard flag
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        Flag<?> existing = registry.get("cm-spawns");

        if (existing instanceof StringFlag) {
            CM_SPAWN_REGIONS = (StringFlag) existing;
        } else
            try {
                //create a flag with the name "my-custom-flag", defaulting to an empty String
                StringFlag flag = new StringFlag("cm-spawns", "");
                registry.register(flag);
                CM_SPAWN_REGIONS = flag; // only set our field if there was no error
            } catch (FlagConflictException e) {
                //some other plugin registered a flag by the same name already.
                //you can use the existing flag, but this may cause conflicts - be sure to check type
                e.printStackTrace();
            }
    }

    @Override
    public void onEnable() {
        getLogger().info("On enable has been called");
        getLogger().info(ChatColor.DARK_PURPLE + CustomType.getTypes().toString());
        saveDefaultConfig();
        loadConfigManager();

//        getLogger().info("Before:\n" + ChatColor.GOLD + CustomType.getTypes().toString());
        CustomType.registerTypes(); //not clean but mob ids need to be registered with the server on startup or the mobs get removed

        // Plugin startup logic
        context = new MobzyContext(getConfig(), mobzyConfig); //Create new context and add plugin and logger to it
        context.setPlugin(this);
        context.setLogger(getLogger());


        //Register events
        getServer().getPluginManager().registerEvents(new MobListener(context), this);
        getServer().getPluginManager().registerEvents(new GuiListener(), this);

        //Register repeating tasks
        if (MobzyConfig.doMobSpawns()) {
            Runnable spawnTask = new SpawnTask(this);
            getServer().getScheduler().scheduleSyncRepeatingTask(this, spawnTask, 0, 100);
        }

        MobzyCommands commandExecutor = new MobzyCommands(context);
        this.getCommand("mobzy").setExecutor(commandExecutor);

        /*Every loaded custom entity in the world stops relating to the CustomMob class heirarchy after reload, so we
        can't do something like customEntity instanceof CustomMob. Therefore, we "reload" those entities by deleting
        them and copying their NBT data to new entities*/
        int num = 0;

        for (World world : getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getScoreboardTags().contains("customMob2") && !(((CraftEntity) entity).getHandle() instanceof CustomMob)) {
                    EntityLiving nmsEntity = (EntityLiving) ((CraftEntity) entity).getHandle();
                    try {
                        EntityLiving replacement = (EntityLiving) ((CraftEntity) CustomType.spawnEntity(CustomType.getType(nmsEntity.getScoreboardTags()), entity.getLocation())).getHandle();
                        NBTTagCompound nbt = new NBTTagCompound();
                        nmsEntity.b(nbt); //.b copies over the entity's nbt data to the compound
                        entity.remove();
                        replacement.a(nbt); //.a copies the nbt data to the new entity
                        num++;
                    } catch (Exception e) {
                        /*if any error ever occurs, just go on with removing the entity that caused the error, otherwise
                        the plugin won't load*/
                    }
                } else if (entity.getScoreboardTags().contains("additionalPart"))
                    entity.remove();
            }
        }
        getLogger().info(ChatColor.GREEN + "Loaded " + num + " custom entities on startup");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // Plugin shutdown logic
        getLogger().info("onDisable has been invoked!");
    }

    public MobzyConfig getMobzyConfig() {
        return mobzyConfig;
    }

    public void loadConfigManager() {
        mobzyConfig = new MobzyConfig(this);
    }
}