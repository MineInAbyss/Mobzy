package com.offz.spigot.custommobs.Mobs.Type;

import com.offz.spigot.custommobs.Mobs.Behaviours.MobBehaviour;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public interface MobType {
    Map<MobTypeKey, MobType> registeredMobs = new HashMap<>();

    String getName();

    static String toEntityTypeID(String name) {
        return name.toLowerCase().replace(' ', '_');
    }

    default String getEntityTypeID() {
        return toEntityTypeID(getName());
    }

    short getModelID();

    Material getMaterial();

    Class getEntityClass();

    Function<? super World, ? extends net.minecraft.server.v1_13_R2.Entity> getEntityFromClass();

    MobBehaviour getBehaviour();

    static void registerMobType(MobType type) {
        registeredMobs.put(type.getKey(), type);
    }

    static void unregisterAllMobs() {
        registeredMobs.clear();
    }

    static MobType getRegisteredMobType(Entity e) {
        if (e.getCustomName() == null)
            return null;
        if (!e.getScoreboardTags().contains("customMob")) {
            return null;
        }

        Set name = e.getScoreboardTags();
        return getRegisteredMobType(name);
    }

    static MobType getRegisteredMobType(String name) {
        /* our entity name always gets converted into an ID so we can use either one to get the MobType
        the idea is to always treat names as IDs internally, so we can avoid problems with spaces
        (and follow Minecraft's existing naming conventions)
        We let anything outside use either one since when we create new mobs we set their customName to their name,
        but must treat their tags with IDs*/
        return getRegisteredMobType(new MobTypeKey(toEntityTypeID(name)));
    }

    static MobType getRegisteredMobType(MobTypeKey key) {
        return registeredMobs.get(key);
    }

    static MobType getRegisteredMobType(Set<String> tags) {
        for (String tag : tags) {
            MobType type = getRegisteredMobType(tag);
            if (type != null)
                return type;
        }
        return null;
    }

    default MobTypeKey getKey() {
        return new MobTypeKey(getName());
    }


    class MobTypeKey { //TODO: Figure out if we really need an EntityTypeID, and implement it better
        private String name;
//        private short modelID; //May add this back later, but it's not too good of entity way to identify mobs

        public MobTypeKey(String name) {
            this.name = toEntityTypeID(name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof MobTypeKey))
                return false;
            MobTypeKey other = (MobTypeKey) o;
            return this.name.equals(other.name);
        }
    }
}