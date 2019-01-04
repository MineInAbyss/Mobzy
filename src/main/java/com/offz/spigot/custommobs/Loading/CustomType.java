package com.offz.spigot.custommobs.Loading;

import com.mojang.datafixers.types.Type;
import com.offz.spigot.custommobs.Mobs.Fuwagi;
import com.offz.spigot.custommobs.Mobs.Neritantan;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import net.minecraft.server.v1_13_R2.*;

import java.util.Map;
import java.util.function.Function;

public class CustomType {
    // this is where we store our custom entity type (for use with spawning, etc)
    public static EntityTypes NERITANTAN;
    public static EntityTypes FUWAGI;

    public static void registerAllMobs() {
        // register the custom entity in the server
        NERITANTAN = injectNewEntity("custom_zombie", "zombie", Neritantan.class, Neritantan::new);
        FUWAGI = injectNewEntity("custom_zombie", "zombie", Fuwagi.class, Fuwagi::new);
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

