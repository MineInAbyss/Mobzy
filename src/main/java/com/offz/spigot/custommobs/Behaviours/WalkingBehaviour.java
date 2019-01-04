package com.offz.spigot.custommobs.Behaviours;

import com.offz.spigot.custommobs.Mobs.Behaviours.MobBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface WalkingBehaviour extends MobBehaviour {
    Map<UUID, MobInfo> registeredMobs = new HashMap<>();

    static void registerMob(Entity e, MobType mobType, short stillDamageValue) { //Models follow a format of Regular, Walking, and Hit model
        registeredMobs.put(e.getUniqueId(), new MobInfo(mobType, stillDamageValue, (short) (stillDamageValue + 1), (short) (stillDamageValue + 2), e));
    }

    static void registerMob(Entity e, MobType mobType, short stillDamageValue, short movingDamageValue, short hitDamageValue) { //Used for special cases if model does not follow normal format
        registeredMobs.put(e.getUniqueId(), new MobInfo(mobType, stillDamageValue, movingDamageValue, hitDamageValue, e));
    }

    static void unregisterMob(UUID uuid) {
        registeredMobs.remove(uuid);
    }

    static void loadRegistered(Map<UUID, MobInfo> map) {
        registeredMobs.clear();
        registeredMobs.putAll(map);
    }

    default void animate(MobInfo mob) {
        Monster e = (Monster) mob.entity;
        try {
            ArmorStand as = ((ArmorStand) e.getPassengers().get(0).getPassengers().get(0));
            EntityEquipment ee = as.getEquipment();

            ItemStack is = ee.getHelmet();
            if (is.getDurability() == mob.hitDamageValue)
                return;
            if (e.getVelocity().getX() == 0 && e.getVelocity().getZ() == 0)
                is.setDurability(mob.stillDamageValue);
            else
                is.setDurability(mob.movingDamageValue);
            ee.setHelmet(is);

            as.setHeadPose(new EulerAngle(0, Math.toRadians(e.getLocation().getYaw() - as.getLocation().getYaw()), 0));
        } catch (IndexOutOfBoundsException exception) { //One frame gets run without the ArmorStand because of the 1 tick delay, causing an error, maybe there is a better way we should handle it, this works for now
        }
    }

    default void onDeath(UUID uuid) {
        unregisterMob(uuid);
    }

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
