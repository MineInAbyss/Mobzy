package com.offz.spigot.custommobs.Mobs.Behaviours;

import com.offz.spigot.custommobs.CustomMobsAPI;
import com.offz.spigot.custommobs.Mobs.CustomMob;
import net.minecraft.server.v1_13_R2.DamageSource;
import net.minecraft.server.v1_13_R2.EntityLiving;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_13_R2.event.CraftEventFactory;

public class Deathable extends MobBehaviour {
    public Deathable(CustomMob mob) {
        super(mob);
    }

    public void die(DamageSource damageSource) {
        EntityLiving entity = mob.getEntity();

        if (!mob.getKilled()) {
            CustomMobsAPI.debug(ChatColor.RED + mob.getBuilder().getName() + " died at coords " + (int) mob.getX() + " " + (int) mob.getY() + " " + (int) mob.getZ());
            EntityLiving killer = mob.getKiller();
            if (mob.getKillScore() >= 0 && killer != null)
                killer.a(entity, mob.getKillScore(), damageSource);

            //TODO: This causes the entity to send a statistics update on death (we don't want this),
            // make sure it doesn't do anything else
            /*if (entity != null)
                entity.b(this);*/

            mob.setKilled(true);

            entity.getCombatTracker().g();
            if (!mob.getWorld().isClientSide) {
                if (mob.dropsExperience() && mob.getWorld().getGameRules().getBoolean("doMobLoot")) {
                    mob.dropEquipment(mob.lastDamageByPlayerTime() > 0, 0, damageSource);
                    CraftEventFactory.callEntityDeathEvent(entity, mob.getBuilder().getDrops());
                } else {
                    CraftEventFactory.callEntityDeathEvent(entity);
                }
            }

            mob.getWorld().broadcastEntityEffect(entity, (byte) 3);
        }
    }
}
