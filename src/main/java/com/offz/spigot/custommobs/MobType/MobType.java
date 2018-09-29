package com.offz.spigot.custommobs.MobType;

import com.offz.spigot.custommobs.Mobs.MobBehaviour;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public interface MobType {
    Map<MobTypeKey, MobType> registeredMobs = new HashMap<>();

    String getName();

    MobBehaviour getBehaviour();

//    String getTypeName();

    static void registerMobType(MobType type) {
        registeredMobs.put(type.getKey(), type);
    }

    static void unregisterAllMobs() {
        registeredMobs.clear();
    }

    static MobType getRegisteredMobType(Entity e) {
        String name = e.getCustomName();
        if (!e.getScoreboardTags().contains("customMob")) {
            return null;
        }
        return getRegisteredMobType(name);
    }

    static MobType getRegisteredMobType(String name) {
        return getRegisteredMobType(new MobTypeKey(name));
    }

    static MobType getRegisteredMobType(MobTypeKey key) {
        return registeredMobs.get(key);
    }

    default MobTypeKey getKey() {
        return new MobTypeKey(getName());
    }


    class MobTypeKey {
        private String name;

        public MobTypeKey (String name) {
            this.name = name;
        }

        @Override
        public int hashCode() {
            return name.hashCode() /*^ typeName.hashCode()*/;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof MobTypeKey))
                return false;
            MobTypeKey other = (MobTypeKey) o;
            return /*this.typeName.equals(other.typeName) &&*/ this.name.equals(other.name);
        }
    }
}
