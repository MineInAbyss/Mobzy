package com.offz.spigot.custommobs.Loading;

import com.mojang.datafixers.types.Type;
import com.offz.spigot.custommobs.Mobs.*;
import com.offz.spigot.custommobs.Mobs.NPCs.Marulk;
import com.offz.spigot.custommobs.Mobs.NPCs.Mitty;
import com.offz.spigot.custommobs.Mobs.NPCs.Nanachi;
import com.offz.spigot.custommobs.Mobs.NPCs.Ozen;
import com.offz.spigot.custommobs.Mobs.Type.FlyingMobType;
import com.offz.spigot.custommobs.Mobs.Type.GroundMobType;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import com.offz.spigot.custommobs.Mobs.Type.NPCMobType;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CustomType {
    // this is where we store our custom entity type (for use with spawning, etc)

    public static Map<String, EntityTypes> ground = new HashMap<>();
    public static Map<String, EntityTypes> flying = new HashMap<>();
    public static Map<String, EntityTypes> npc = new HashMap<>();
//    public static EntityTypes NERITANTAN;
//    public static EntityTypes FUWAGI;
//    public static EntityTypes INBYO;
//    public static EntityTypes ROHANA;

//    public static EntityTypes CORPSE_WEEPER;

//    public static EntityTypes MITTY;
//    public static EntityTypes NANACHI;
//    public static EntityTypes OZEN;
//    public static EntityTypes MARULK;

    public static void registerAllMobs() {
        // register the custom entity in the server
        for (GroundMobType mob : GroundMobType.values()) {
            String name = mob.getName().toLowerCase().replace(' ', '_');
            Class entityClass = mob.getEntityClass();
            Function<? super World, ? extends Entity> entityFromClass = mob.getEntityFromClass();
            ground.put(name.toUpperCase(), injectNewEntity(name, "zombie", entityClass, entityFromClass));
        }
        for (FlyingMobType mob : FlyingMobType.values()) {
            String name = mob.getName().toLowerCase().replace(' ', '_');
            Class entityClass = mob.getEntityClass();
            Function<? super World, ? extends Entity> entityFromClass = mob.getEntityFromClass();
            flying.put(name.toUpperCase(), injectNewEntity(name, "zombie", entityClass, entityFromClass));
        }
        for (NPCMobType mob : NPCMobType.values()) {
            String name = mob.getName().toLowerCase().replace(' ', '_');
            Class entityClass = mob.getEntityClass();
            Function<? super World, ? extends Entity> entityFromClass = mob.getEntityFromClass();
            npc.put(name.toUpperCase(), injectNewEntity(name, "zombie", entityClass, entityFromClass));
        }
    }
    public static void unloadAllMobs() {
        // Unregister
        MobType.unregisterAllMobs();

        // Destroy
//        mobClassLoader = null;
    }

    private static EntityTypes injectNewEntity(String name, String extend_from, Class<? extends Entity> clazz, Function<? super World, ? extends Entity> function) { //from https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293
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

}

