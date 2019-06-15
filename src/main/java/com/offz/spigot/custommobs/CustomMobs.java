package com.offz.spigot.custommobs;

import com.derongan.minecraft.mineinabyss.AbyssContext;
import com.derongan.minecraft.mineinabyss.MineInAbyss;
import com.offz.spigot.custommobs.Listener.MobListener;
import com.offz.spigot.custommobs.Loading.CustomType;
import com.offz.spigot.custommobs.Mobs.CustomMob;
import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomMobs extends JavaPlugin {

    private MobContext context;

    @Override
    public void onLoad() {
//        CustomType.registerAllMobs();
    }

    @Override
    public void onEnable() {
        getLogger().info("On enable has been called");
        CustomType.registerAllMobs();
        saveDefaultConfig();
        CustomMobsAPI.loadConfigValues(this);
        AbyssContext abyssContext = MineInAbyss.getContext();
        // Plugin startup logic
        context = new MobContext(getConfig()); //Create new context and add plugin and logger to it
        context.setPlugin(this);
        context.setLogger(getLogger());


        //Register events
        getServer().getPluginManager().registerEvents(new MobListener(context), this);
//        getServer().getPluginManager().registerEvents(new SpawnListener(abyssContext), this);

        //Register repeating tasks
//        Runnable mobTask = new MobTask();
//        getServer().getScheduler().scheduleSyncRepeatingTask(this, mobTask, 0, 1);
//        Runnable spawnTask = new SpawnTask(this, abyssContext);
//        getServer().getScheduler().scheduleSyncRepeatingTask(this, spawnTask, 0, 100);

        CMCommandExecutor commandExecutor = new CMCommandExecutor(context);

        this.getCommand("cmrm").setExecutor(commandExecutor);
        this.getCommand("cminfo").setExecutor(commandExecutor);
        this.getCommand("cms").setExecutor(commandExecutor);
        this.getCommand("cml").setExecutor(commandExecutor);

        int num = 0;

        /*Every loaded custom entity in the world stops relating to the CustomMob class heirarchy after reload, so we
        can't do something like customEntity instanceof CustomMob. Therefore, we "reload" those entities by deleting
        them and copying their NBT data to new entities
         */
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
                    } catch (Exception e) {
                        /*if any error ever occurs, just go on with removing the entity that caused the error, otherwise
                        the plugin won't load*/
                    }
                } else if (entity.getScoreboardTags().contains("additionalPart"))
                    entity.remove();
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