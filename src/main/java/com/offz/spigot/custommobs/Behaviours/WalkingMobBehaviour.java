package com.offz.spigot.custommobs.Behaviours;

import com.offz.spigot.custommobs.Mobs.Type.MobType;
import com.offz.spigot.custommobs.Mobs.Behaviours.MobBehaviour;
import org.bukkit.entity.Entity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public interface WalkingMobBehaviour extends MobBehaviour {
    Map<UUID, MobInfo> registeredMobs = new ConcurrentHashMap<>();

    static void registerMob(Entity e, MobType mobType, short stillDamageValue) { //Models follow a format of Regular, Walking, and Hit model
        registeredMobs.put(e.getUniqueId(), new MobInfo(mobType, stillDamageValue, (short) (stillDamageValue + 1), (short) (stillDamageValue + 2), e));
    }

    static void registerMob(Entity e, MobType mobType, short stillDamageValue, short movingDamageValue, short hitDamageValue) { //Used for special cases if model does not follow normal format
        registeredMobs.put(e.getUniqueId(), new MobInfo(mobType, stillDamageValue, movingDamageValue, hitDamageValue, e));
    }

    /*static void unregisterMob(Entity e){ //TODO: UNREGISTER MOB
        registeredMobs.remove(new MobInfo(mobType, stillDamageValue, (short) (stillDamageValue + 1));
    }*/

    void animate(MobInfo mobInfo);

    class MobInfo {
        public MobType mobType;
        public short stillDamageValue;
        public short movingDamageValue;
        public short hitDamageValue;
        public Object entity;

        public MobInfo(MobType mobType, short stillDamageValue, short movingDamageValue, short hitDamageValue, Object entity) {
            this.mobType = mobType;
            this.stillDamageValue = stillDamageValue;
            this.movingDamageValue = movingDamageValue;
            this.hitDamageValue = hitDamageValue;
            this.entity = entity;
        }
    }
}
