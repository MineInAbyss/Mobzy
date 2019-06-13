package com.offz.spigot.custommobs.Loading;

import com.mojang.datafixers.types.Type;
import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Mobs.Behaviours.AfterSpawnBehaviour;
import com.offz.spigot.custommobs.Mobs.Hostile.Inbyo;
import com.offz.spigot.custommobs.Mobs.Hostile.Rohana;
import com.offz.spigot.custommobs.Mobs.Passive.Fuwagi;
import com.offz.spigot.custommobs.Mobs.Passive.NPC;
import com.offz.spigot.custommobs.Mobs.Passive.Neritantan;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@SuppressWarnings("SpellCheckingInspection")
public class CustomType {
    public static EntityTypes NERITANTAN;
    public static EntityTypes FUWAGI;

    //Hostile
    public static EntityTypes INBYO;
    public static EntityTypes ROHANA;

    //NPCs
    public static EntityTypes MITTY, NANACHI, BONDREWD, HABO, JIRUO, KIYUI, MARULK, NAT, OZEN, REG, RIKO, SHIGGY, TORKA;

    //this is used for getting a MobType from a String, which makes it easier to access from MobBuilder
    private static Map<String, EntityTypes> types = new HashMap<>();

    public static void registerAllMobs() {
        NERITANTAN = registerEntity(Neritantan.class, Neritantan::new);
        FUWAGI = registerEntity(Fuwagi.class, Fuwagi::new);

        //Hostile
        INBYO = registerEntity(Inbyo.class, Inbyo::new);
        INBYO = registerEntity(Rohana.class, Rohana::new);


        //NPCs
        MITTY = registerEntity("mitty", NPC.class, (world -> new NPC(world, "Mitty", 2)));
        NANACHI = registerEntity("nanachi", NPC.class, (world -> new NPC(world, "Nanachi", 3)));
        BONDREWD = registerEntity("bondrewd", NPC.class, (world -> new NPC(world, "Bondrewd", 4)));
        HABO = registerEntity("habo", NPC.class, (world -> new NPC(world, "Habo", 5)));
        JIRUO = registerEntity("jiruo", NPC.class, (world -> new NPC(world, "Jiruo", 6)));
        KIYUI = registerEntity("kiyui", NPC.class, (world -> new NPC(world, "Kiyui", 7)));
        MARULK = registerEntity("marulk", NPC.class, (world -> new NPC(world, "Marulk", 8)));
        NAT = registerEntity("nat", NPC.class, (world -> new NPC(world, "Nat", 9)));
        OZEN = registerEntity("ozen", NPC.class, (world -> new NPC(world, "Ozen", 10)));
        REG = registerEntity("reg", NPC.class, (world -> new NPC(world, "Reg", 11)));
        RIKO = registerEntity("riko", NPC.class, (world -> new NPC(world, "Riko", 12)));
        SHIGGY = registerEntity("shiggy", NPC.class, (world -> new NPC(world, "Shiggy", 13)));
        TORKA = registerEntity("torka", NPC.class, (world -> new NPC(world, "Torka", 14)));
    }

    public static String toEntityTypeID(String name) {
        return name.toLowerCase().replace(' ', '_');
    }

    public static EntityTypes getType(Set<String> tags) {
        for (String tag : tags)
            if (types.containsKey(toEntityTypeID(tag)))
                return types.get(toEntityTypeID(tag));
        return null;
    }

    public static EntityTypes getType(String name) {
        return types.get(toEntityTypeID(name));
    }

    public static EntityTypes getType(MobBuilder builder) {
        return types.get(toEntityTypeID(builder.getName()));
    }

    public static Map<String, EntityTypes> getTypes() {
        return types;
    }

    private static EntityTypes registerEntity(Class entityClass, Function<? super World, ? extends Entity> entityFromClass) {
        String name = toEntityTypeID(entityClass.getSimpleName());
        return registerEntity(name, entityClass, entityFromClass);
    }

    private static EntityTypes registerEntity(String name, Class entityClass, Function<? super World, ? extends Entity> entityFromClass) {
        return types.put(name, injectNewEntity(name, "zombie", entityClass, entityFromClass));
    }

    //from https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293
    private static EntityTypes injectNewEntity(String name, String extend_from, Class<? extends Entity> clazz, Function<? super World, ? extends Entity> function) { //from https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293
        // get the server's datatypes (also referred to as "data fixers" by some)
        // I still don't know where 15190 came from exactly, when entity few of us
        // put our heads together that's the number someone else came up with
        Map<Object, Type<?>> dataTypes = (Map<Object, Type<?>>) DataConverterRegistry.a().getSchema(15190).findChoiceType(DataConverterTypes.n).types();
        // inject the new custom entity (this registers the name/id with the server so you can use it in things
        // like the vanilla /summon command)
        dataTypes.put("minecraft:" + name, dataTypes.get("minecraft:" + extend_from));
        // create and return an EntityTypes for the custom entity store this somewhere so you can reference it later (like for spawning)
        return EntityTypes.a(name, EntityTypes.a.a(clazz, function));
    }

    public static org.bukkit.entity.Entity spawnEntity(String name, Location loc) {
        EntityTypes entityTypes = CustomType.getType(name);
        return entityTypes == null ? null : spawnEntity(entityTypes, loc);
    }

    /**
     * Spawns entity at specified Location
     *
     * @param entityTypes type of entity to spawn
     * @param loc  Location to spawn at
     * @return Reference to the spawned bukkit Entity
     */
    public static org.bukkit.entity.Entity spawnEntity(EntityTypes entityTypes, Location loc) {
        net.minecraft.server.v1_13_R2.Entity nmsEntity = entityTypes.spawnCreature( // NMS method to spawn an entity from an EntityTypes
                ((CraftWorld) loc.getWorld()).getHandle(), // reference to the NMS world
                null, // EntityTag NBT compound
                null, // custom name of entity
                null, // player reference. used to know if player is OP to apply EntityTag NBT compound
                new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), // the BlockPosition to spawn at
                true, // center entity on BlockPosition and correct Y position for Entity's height
                false,
                CreatureSpawnEvent.SpawnReason.CUSTOM); // not sure. alters the Y position. this is only ever true when using spawn egg and clicked face is UP

        //Call a method after the entity has been spawned and things like location have been determined
        if(nmsEntity instanceof AfterSpawnBehaviour)
            ((AfterSpawnBehaviour) nmsEntity).afterSpawn();

        return nmsEntity == null ? null : nmsEntity.getBukkitEntity(); // convert to a Bukkit entity
    }
}

