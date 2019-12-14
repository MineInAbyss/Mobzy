package com.offz.spigot.mobzy;

import com.mojang.datafixers.types.Type;
import com.offz.spigot.mobzy.mobs.MobTemplate;
import com.offz.spigot.mobzy.mobs.behaviours.AfterSpawnBehaviour;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@SuppressWarnings("SpellCheckingInspection")
public class CustomType {
    //this is used for getting a MobType from a String, which makes it easier to access from MobBuilder
    private static Map<String, EntityTypes> types = new HashMap<>();
    private static Map<String, MobTemplate> builders = new HashMap<>();
    private static Mobzy plugin = Mobzy.getInstance();

    public static String toEntityTypeID(String name) {
        return name.toLowerCase().replace(" ", "");
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

    public static MobTemplate getBuilder(String name) {
        return builders.get(toEntityTypeID(name));
    }

    public static EntityTypes getType(MobTemplate builder) {
        return types.get(toEntityTypeID(builder.getName()));
    }

    public static Map<String, EntityTypes> getTypes() {
        return types;
    }

    protected static EntityTypes registerEntity(Class entityClass, Function<? super World, ? extends Entity> entityFromClass) {
        String name = toEntityTypeID(entityClass.getSimpleName());
        return registerEntity(name, entityClass, entityFromClass);
    }

    protected static EntityTypes registerEntity(String name, Class entityClass, Function<? super World, ? extends Entity> entityFromClass) {
        EntityTypes injected = injectNewEntity(name, "zombie", entityClass, entityFromClass);
        types.put(name, injected);

        return injected;
    }

    public static void registerTypes() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Registering types");
        for (EntityTypes type : types.values()) {
            String className = type.c().getSimpleName();
            String name = toEntityTypeID(className);

            builders.put(name, readBuilderConfig(className));
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + types.keySet().toString());
    }

    protected static MobTemplate readBuilderConfig(String className) {
        FileConfiguration mobCfg = null;
        className = className.substring(0, 1) + className.toLowerCase().substring(1); //config can't read mobs if a capital is present anywhere after the first character, so we set them all to lowercase

        for (FileConfiguration readCfg : plugin.getMobzyConfig().getMobCfgs().values()) {
            if (readCfg.contains(className)) {
                mobCfg = readCfg;
                break;
            }
        }

        if (mobCfg == null)
            return null;

        return MobTemplate.Companion.deserialize(((MemorySection) mobCfg.get(className)).getValues(true), className);
    }

    //from https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293
    private static EntityTypes injectNewEntity(String name, String extend_from, Class<? extends Entity> clazz, Function<? super World, ? extends Entity> function) { //from https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293
        // get the server's datatypes (also referred to as "data fixers" by some)
        // I still don't know where 15190 came from exactly, when entity few of us
        // put our heads together that's the number someone else came up with
        Map<Object, Type<?>> dataTypes = (Map<Object, Type<?>>) DataConverterRegistry.a().getSchema(15190).findChoiceType(DataConverterTypes.n).types();
        // inject the new custom entity (this registers the name/id with the server so you can use it in things
        // like the vanilla /summon command)
        if (dataTypes.containsKey("minecraft:" + name))
            MobzyAPI.debug(ChatColor.YELLOW + "ALREADY CONTAINS KEY: " + name);
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
     * @param loc         Location to spawn at
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
        if (nmsEntity instanceof AfterSpawnBehaviour)
            ((AfterSpawnBehaviour) nmsEntity).afterSpawn();

        //testing the enoughSpace method
//        if (!MobSpawn.enoughSpace(loc, nmsEntity.width, nmsEntity.length))
//            nmsEntity.die();

        return nmsEntity == null ? null : nmsEntity.getBukkitEntity(); // convert to a Bukkit entity
    }
}

