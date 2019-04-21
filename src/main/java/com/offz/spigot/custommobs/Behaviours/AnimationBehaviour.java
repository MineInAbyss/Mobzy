package com.offz.spigot.custommobs.Behaviours;

import com.offz.spigot.custommobs.Mobs.Behaviours.MobBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface AnimationBehaviour extends MobBehaviour {
    Map<UUID, MobInfo> registeredMobs = new HashMap<>();

    static void registerMob(Entity e, MobType mobType, short stillDamageValue) { //Models follow entity format of Regular, Walking, and Hit model
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
        LivingEntity e = (LivingEntity) mob.entity;
        //TODO: Fix this
//        if (e.isDead()) {
//            unregisterMob(e.getUniqueId());
//            return;
//        }

        MobBehaviour behaviour = mob.mobType.getBehaviour();

        if (behaviour instanceof HeadRotateBehaviour) {
            ((HeadRotateBehaviour) behaviour).rotateHead(mob);
            try {
                ArmorStand as = ((ArmorStand) e.getPassengers().get(0).getPassengers().get(0));
                as.setHeadPose(new EulerAngle(0, Math.toRadians(e.getLocation().getYaw() - as.getLocation().getYaw()), 0));
            } catch (IndexOutOfBoundsException iobe) {
            }
        }

        if (behaviour instanceof WalkingBehaviour) {
            ((WalkingBehaviour) behaviour).walk(mob);
            Vector v = e.getVelocity();
            try {
                AreaEffectCloud aec = (AreaEffectCloud) e.getPassengers().get(0);
                ArmorStand as = ((ArmorStand) aec.getPassengers().get(0));
                EntityEquipment ee = as.getEquipment();
                /*CraftEntity entity = (CraftEntity) mob.entity;
                net.minecraft.server.v1_13_R2.Entity lookAt = entity.getHandle();
                Location l = e.getLocation();
                lookAt.setPositionRotation(l.getX(), l.getY(), l.getZ(), 1, 1);*/

                ItemStack is = ee.getHelmet();
                if (is.getDurability() == mob.hitDamageValue)
                    return;
                if (v.getX() == 0 && v.getZ() == 0) //TODO: Fix mobs switching to moving animation from one frame while taking damage
                    is.setDurability(mob.stillDamageValue);
                else
                    is.setDurability(mob.movingDamageValue);
                ee.setHelmet(is);
            } catch (IndexOutOfBoundsException exception) { //if the mob does not have any passengers, i.e. is not from SpawnModelBehaviour
               if (!(behaviour instanceof SpawnModelBehaviour)) {
                    EntityEquipment ee = e.getEquipment();
                    ItemStack is = ee.getHelmet();
                    if (is.getDurability() == mob.hitDamageValue)
                        return;
                    if (e.getVelocity().getX() == 0 && e.getVelocity().getZ() == 0)
                        is.setDurability(mob.stillDamageValue);
                    else
                        is.setDurability(mob.movingDamageValue);
                    ee.setHelmet(is);
                }
            }
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
