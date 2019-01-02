package com.offz.spigot.custommobs;

import com.mojang.datafixers.types.Type;
import com.offz.spigot.custommobs.Behaviours.MoveAnimationTask;
import com.offz.spigot.custommobs.Behaviours.Listener.MobHitListener;
import com.offz.spigot.custommobs.Loading.MobLoader;
import com.offz.spigot.custommobs.Mobs.Neritantan;
import com.offz.spigot.custommobs.Spawning.SpawnListener;
import net.minecraft.server.v1_13_R2.DataConverterRegistry;
import net.minecraft.server.v1_13_R2.DataConverterTypes;
import net.minecraft.server.v1_13_R2.EntityTypes;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.function.Function;

public final class CustomMobs extends JavaPlugin {

    private MobContext context;

    // this is where we store our custom entity type (for use with spawning, etc)
    public static EntityTypes CUSTOM_ZOMBIE;

    @Override
    public void onEnable(){
        // Plugin startup logic
        context = new MobContext(getConfig()); //Create new context and add plugin and logger to it
        context.setPlugin(this);
        context.setLogger(getLogger());


        getLogger().info("On enable has been called");

        //Register events
        getServer().getPluginManager().registerEvents(new MobHitListener(context), this);
        getServer().getPluginManager().registerEvents(new SpawnListener(), this);

        //Register repeating tasks
        Runnable mobTask = new MobTask();
        getServer().getScheduler().scheduleSyncRepeatingTask(this, mobTask, 0, 1);
        Runnable moveAnimationTask = new MoveAnimationTask();
        getServer().getScheduler().scheduleSyncRepeatingTask(this, moveAnimationTask, 0, 1);

        MobLoader.loadAllMobs(context);

        // register the custom entity in the server
        // it is recommended to do this when the server is loading
        // but since we're not replacing vanilla entities it can be
        // done later if wanted
        CUSTOM_ZOMBIE = injectNewEntity("custom_zombie", "zombie", Neritantan.class, Neritantan::new);
    }

    private EntityTypes injectNewEntity(String name, String extend_from, Class<? extends net.minecraft.server.v1_13_R2.Entity> clazz, Function<? super World, ? extends net.minecraft.server.v1_13_R2.Entity> function) { //from https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293
        // get the server's datatypes (also referred to as "data fixers" by some)
        // I still don't know where 15190 came from exactly, when a few of us
        // put our heads together that's the number someone else came up with
        Map<Object, Type<?>> dataTypes = (Map<Object, Type<?>>) DataConverterRegistry.a().getSchema(15190).findChoiceType(DataConverterTypes.n).types();
        // inject the new custom entity (this registers the
        // name/id with the server so you can use it in things
        // like the vanilla /summon command)
        dataTypes.put("minecraft:" + name, dataTypes.get("minecraft:" + extend_from));
        // create and return an EntityTypes for the custom entity
        // store this somewhere so you can reference it later (like for spawning)
        return EntityTypes.a(name, EntityTypes.a.a(clazz, function));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("onDisable has been invoked!");
    }
}