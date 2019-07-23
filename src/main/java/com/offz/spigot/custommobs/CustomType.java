package com.offz.spigot.custommobs;

import com.mojang.datafixers.types.Type;
import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Mobs.Behaviours.AfterSpawnBehaviour;
import com.offz.spigot.custommobs.Mobs.Flying.*;
import com.offz.spigot.custommobs.Mobs.Hostile.*;
import com.offz.spigot.custommobs.Mobs.MobDrop;
import com.offz.spigot.custommobs.Mobs.Passive.Fuwagi;
import com.offz.spigot.custommobs.Mobs.Passive.NPC;
import com.offz.spigot.custommobs.Mobs.Passive.Neritantan;
import com.offz.spigot.custommobs.Mobs.Passive.Okibo;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("SpellCheckingInspection")
public class CustomType {

    //this is used for getting a MobType from a String, which makes it easier to access from MobBuilder
    private static Map<String, EntityTypes> types = new HashMap<>();
    //Passive
    public static final EntityTypes NERITANTAN = registerEntity(Neritantan.class, Neritantan::new);
    public static final EntityTypes OKIBO = registerEntity(Okibo.class, Okibo::new);
    //Hostile
    public static final EntityTypes INBYO = registerEntity(Inbyo.class, Inbyo::new);
    public static final EntityTypes ROHANA = registerEntity(Rohana.class, Rohana::new);
    public static final EntityTypes TAMAGAUCHI = registerEntity(Tamaugachi.class, Tamaugachi::new);
    public static final EntityTypes SILKFANG = registerEntity(Silkfang.class, Silkfang::new);
    public static final EntityTypes KUONGATARI = registerEntity(Kuongatari.class, Kuongatari::new);
    public static final EntityTypes TESUCHI = registerEntity(Tesuchi.class, Tesuchi::new);
    public static final EntityTypes OTTOBAS = registerEntity(Ottobas.class, Ottobas::new);
    //Flying
    public static final EntityTypes CORPSE_WEEPER = registerEntity(CorpseWeeper.class, CorpseWeeper::new);
    public static final EntityTypes MADOKAJACK = registerEntity(Madokajack.class, Madokajack::new);
    public static final EntityTypes HAMMERBEAK = registerEntity(Hammerbeak.class, Hammerbeak::new);
    public static final EntityTypes KAZURA = registerEntity(Kazura.class, Kazura::new);
    public static final EntityTypes BENIKUCHINAWA = registerEntity(Benikuchinawa.class, Benikuchinawa::new);
    //NPCs
    public static final EntityTypes MITTY = registerEntity("mitty", NPC.class, (world -> new NPC(world, "Mitty", 2)));
    public static final EntityTypes NANACHI = registerEntity("nanachi", NPC.class, (world -> new NPC(world, "Nanachi", 3)));
    public static final EntityTypes BONDREWD = registerEntity("bondrewd", NPC.class, (world -> new NPC(world, "Bondrewd", 4)));
    public static final EntityTypes HABO = registerEntity("habo", NPC.class, (world -> new NPC(world, "Habo", 5)));
    public static final EntityTypes JIRUO = registerEntity("jiruo", NPC.class, (world -> new NPC(world, "Jiruo", 6)));
    public static final EntityTypes KIYUI = registerEntity("kiyui", NPC.class, (world -> new NPC(world, "Kiyui", 7)));
    public static final EntityTypes MARULK = registerEntity("marulk", NPC.class, (world -> new NPC(world, "Marulk", 8)));
    public static final EntityTypes NAT = registerEntity("nat", NPC.class, (world -> new NPC(world, "Nat", 9)));
    public static final EntityTypes OZEN = registerEntity("ozen", NPC.class, (world -> new NPC(world, "Ozen", 10)));
    public static final EntityTypes REG = registerEntity("reg", NPC.class, (world -> new NPC(world, "Reg", 11)));
    public static final EntityTypes RIKO = registerEntity("riko", NPC.class, (world -> new NPC(world, "Riko", 12)));
    public static final EntityTypes SHIGGY = registerEntity("shiggy", NPC.class, (world -> new NPC(world, "Shiggy", 13)));
    public static final EntityTypes TORKA = registerEntity("torka", NPC.class, (world -> new NPC(world, "Torka", 14)));
    public static final EntityTypes LYZA = registerEntity("lyza", NPC.class, (world -> new NPC(world, "Lyza", 15)));
    public static final EntityTypes PRUSHKA = registerEntity("prushka", NPC.class, (world -> new NPC(world, "Prushka", 16)));
    private static Map<String, MobBuilder> builders = new HashMap<>();
    public static final EntityTypes FUWAGI = registerEntity(Fuwagi.class, Fuwagi::new);
    private static CustomMobs plugin;

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

    public static MobBuilder getBuilder(String name) {
        return builders.get(toEntityTypeID(name));
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
        EntityTypes injected = injectNewEntity(name, "zombie", entityClass, entityFromClass);
        types.put(name, injected);

        return injected;
    }

    public static void registerTypes(CustomMobs plugin) {
        //TODO make sure we don't need to unregister things
//        Map<Object, Type<?>> dataTypes = (Map<Object, Type<?>>) DataConverterRegistry.a().getSchema(15190).findChoiceType(DataConverterTypes.n).types();
//        dataTypes.keySet()
//        Bukkit.broadcastMessage();
        for (EntityTypes type : types.values()) {
            String className = type.c().getSimpleName();
            String name = toEntityTypeID(className);

            //some entities reuse the same class (such as NPCs) and pass different parameters in the constructor. These should only be added once
            if (!builders.containsKey(name))
                builders.put(name, readBuilderConfig(className, plugin));
        }
    }

    private static MobBuilder readBuilderConfig(String className, CustomMobs plugin) {
        FileConfiguration mobCfg = plugin.getConfigManager().getMobsCfg();
        className = className.substring(0, 1) + className.toLowerCase().substring(1); //config can't read mobs if a capital is present anywhere after the first character, so we set them all to lowercase
        String name = className;

        if (mobCfg.contains(className + ".name"))
            name = mobCfg.getString(className + ".name");

        MobBuilder builder = new MobBuilder(name, mobCfg.getInt(className + ".model"));

        if (mobCfg.contains(className + ".adult"))
            builder.setAdult(mobCfg.getBoolean(className + ".adult"));
        if (mobCfg.contains(className + ".disguise_as"))
            builder.setDisguiseAs(DisguiseType.valueOf(mobCfg.getString(className + ".disguise_as")));
        if (mobCfg.contains(className + ".drops"))
            builder.setDrops(mobCfg.getMapList(className + ".drops").stream()
                    .map(drop -> MobDrop.deserialize((Map<String, Object>) drop))
                    .collect(Collectors.toList()));
        if (mobCfg.contains(className + ".model_material"))
            builder.setModelMaterial(Material.getMaterial(mobCfg.getString(className + ".model_material")));
        if (mobCfg.contains(className + ".tempt_items"))
            builder.setTemptItems(mobCfg.getList(className + ".tempt_items").stream()
                    .map(item -> Material.getMaterial((String) item))
                    .collect(Collectors.toList()));
        if (mobCfg.contains(className + ".max_health"))
            builder.setMaxHealth(mobCfg.getDouble(className + ".max_health"));
        if (mobCfg.contains(className + ".movement_speed"))
            builder.setMovementSpeed(mobCfg.getDouble(className + ".movement_speed"));
        if (mobCfg.contains(className + ".attack_damage"))
            builder.setAttackDamage(mobCfg.getDouble(className + ".attack_damage"));
        if (mobCfg.contains(className + ".follow_range"))
            builder.setFollowRange(mobCfg.getDouble(className + ".follow_range"));

        return builder;
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

